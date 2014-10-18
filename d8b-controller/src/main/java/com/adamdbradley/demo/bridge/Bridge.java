package com.adamdbradley.demo.bridge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.adamdbradley.d8b.actor.FullBoot;
import com.adamdbradley.d8b.actor.Reset;
import com.adamdbradley.d8b.audio.AudioControlConnection;
import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.d8b.console.signal.Signal;
import com.adamdbradley.mcu.MCUClientPort;
import com.adamdbradley.mcu.MidiPortHelper;
import com.adamdbradley.mcu.actor.MCUReset;
import com.adamdbradley.mcu.actor.MasterStartup;
import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.protocol.command.MoveFader;
import com.adamdbradley.mcu.console.protocol.signal.ButtonMessage;
import com.adamdbradley.mcu.console.protocol.signal.ButtonPressed;
import com.adamdbradley.mcu.console.protocol.signal.ChannelButtonMessage;
import com.adamdbradley.mcu.console.protocol.signal.ChannelVPotMoved;
import com.adamdbradley.mcu.console.protocol.signal.FaderMoved;
import com.adamdbradley.mcu.console.protocol.signal.FaderReleased;

/**
 * {@link Bridge} implements a pidgin connection between a running MCU
 * and a running d8b, allowing them to manipulate each other's faders,
 * a few LEDs, and a few transport buttons.
 * <p>
 * Mainly to prove to myself that parsing and message transmission works
 * and that the subscription architecture is workable.
 */
public class Bridge
implements Runnable, AutoCloseable {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(8);

    private final MCUClientPort mcuPort;
    private final ConsoleControlConnection console;
    private final AudioControlConnection audio;

    private final Queue<Signal> consoleQueue = new ConcurrentLinkedQueue<>();
    private final Queue<com.adamdbradley.mcu.console.protocol.Signal> mcuQueue = new ConcurrentLinkedQueue<>();

    private final Model model = new Model();


    public Bridge() throws Exception {
        console = new ConsoleControlConnection("COM12");
        audio = new AudioControlConnection("COM14");
        mcuPort = MidiPortHelper.findAndOpenPort();
    }


    @Override
    public void run() {
        try {
            reallyRun();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void reallyRun() throws Exception {
        final List<Future<?>> pending = new ArrayList<>(64);

        final Future<?> d8bBoot = executor.submit(new FullBoot(console, audio));
        final Future<?> mcuBoot = executor.submit(new MasterStartup(mcuPort));
        pending.add(d8bBoot);
        pending.add(mcuBoot);

        while (!pending.isEmpty()) {
            final Iterator<Future<?>> it = pending.iterator();
            try {
                it.next().get();
            } catch (ExecutionException e) {
                throw (Exception) e.getCause();
            }
            it.remove();
        }

        executor.submit(new Reset(console));
        executor.submit(new MCUReset(mcuPort));

        console.subscribe(consoleQueue);
        mcuPort.subscribe(mcuQueue);

        model.connect(console, mcuPort);

        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                processConsole(consoleQueue);
            }
        }, 0, 20, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                processMCU(mcuQueue);
            }
        }, 10, 20, TimeUnit.MILLISECONDS);

        Thread.sleep(120000);

        executor.shutdown();
    }


    private void processConsole(final Queue<Signal> consoleQueue) {
        try {
            while (!consoleQueue.isEmpty()) {
                final Signal signal = consoleQueue.poll();
                if (signal == null) {
                    return;
                }
                switch (signal.type) {
                case Heartbeat1:
                case Heartbeat2:
                    break;
                default:
                    System.out.println("CONSOLE: " + signal);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        }
    }


    private void processMCU(final Queue<com.adamdbradley.mcu.console.protocol.Signal> mcuQueue) {
        try {
            while (!mcuQueue.isEmpty()) {
                final com.adamdbradley.mcu.console.protocol.Signal signal = mcuQueue.poll();

                if (signal == null) {
                    return;
                }
    
                if (signal instanceof ButtonPressed) {
                    switch (((ButtonMessage) signal).button) {
                    case BankLeft:
                        model.mcuMove(-8);
                        break;
                    case BankRight:
                        model.mcuMove(8);
                        break;
                    case ChannelLeft:
                        model.mcuMove(-1);
                        break;
                    case ChannelRight:
                        model.mcuMove(1);
                        break;
                    case Rewind:
                        model.rewind(signal instanceof ButtonPressed);
                        break;
                    case FastFwd:
                        model.fastFwd(signal instanceof ButtonPressed);
                        break;
                    case Stop:
                        model.stop(signal instanceof ButtonPressed);
                        break;
                    case Play:
                        model.play(signal instanceof ButtonPressed);
                        break;
                    case Record:
                        model.record(signal instanceof ButtonPressed);
                        break;
                    default:
                        return;
                    }
                } else if (signal instanceof ChannelButtonMessage) {
                    switch (((ChannelButtonMessage) signal).channelButton) {
                    case REC:
                        model.channelRec(model.mcuViewOffset + ((ChannelButtonMessage) signal).channel.ordinal());
                        break;
                    case SOLO:
                        model.channelSolo(model.mcuViewOffset + ((ChannelButtonMessage) signal).channel.ordinal());
                        break;
                    case MUTE:
                        model.channelMute(model.mcuViewOffset + ((ChannelButtonMessage) signal).channel.ordinal());
                        break;
                    case SELECT:
                        model.channelSelect(model.mcuViewOffset + ((ChannelButtonMessage) signal).channel.ordinal());
                        break;
                    case VPot:
                        model.channelVPot(model.mcuViewOffset + ((ChannelButtonMessage) signal).channel.ordinal());
                        break;
                    default:
                        throw new IllegalArgumentException();
                    }
                } else if (signal instanceof ChannelVPotMoved) {
                    model.channelVPot(model.mcuViewOffset + ((ChannelVPotMoved) signal).channel.ordinal(),
                            ((ChannelVPotMoved) signal).velocity);
                } else if (signal instanceof FaderMoved) {
                    final FaderMoved faderSignal = (FaderMoved) signal;
                    if (faderSignal.fader == Fader.Master) {
                        model.masterFader(faderSignal.value);
                    } else {
                        model.fader(model.mcuViewOffset + faderSignal.fader.ordinal(), faderSignal.value);
                    }
                } else if (signal instanceof FaderReleased) {
                    final FaderReleased faderSignal = (FaderReleased) signal;
                    if (faderSignal.fader == Fader.Master) {
                        mcuPort.send(new MoveFader(faderSignal.fader, model.masterFader));
                    } else {
                        mcuPort.send(new MoveFader(faderSignal.fader, model.channels[faderSignal.fader.ordinal() + model.mcuViewOffset].fader));
                    }
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        }
    }


    @Override
    public void close() throws Exception {
        try {
            mcuPort.close();
        } finally {
            try {
                console.close();
            } finally {
                audio.close();
            }
        }
    }



    public static void main(final String[] argv) throws Exception {
        try (final Bridge bridge = new Bridge()) {
            bridge.run();
        }
    }

}
