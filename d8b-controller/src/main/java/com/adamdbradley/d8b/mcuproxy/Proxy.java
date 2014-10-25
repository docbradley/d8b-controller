package com.adamdbradley.d8b.mcuproxy;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.adamdbradley.d8b.AppClock;
import com.adamdbradley.d8b.Application;
import com.adamdbradley.d8b.actor.FullBoot;
import com.adamdbradley.d8b.actor.Reset;
import com.adamdbradley.d8b.audio.AudioControlConnection;
import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.d8b.console.Fader;
import com.adamdbradley.d8b.console.command.RequestSerialNumber;
import com.adamdbradley.d8b.console.signal.SerialNumber;
import com.adamdbradley.d8b.console.signal.Signal;
import com.adamdbradley.d8b.console.signal.SignalType;
import com.adamdbradley.mcu.MCUEmulatorPort;
import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.SignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.command.SetSignalLevel;
import com.adamdbradley.mcu.console.protocol.command.WriteScreen;
import com.adamdbradley.mcu.console.protocol.signal.ReportAwake;

/**
 * Use the d8b to simulate an MCU master and two MCU XTs.
 * TODO: Also use the control section to implement audio routing control.
 * TODO MAYBE: Also use part of the control section to simulate part of a C4?
 */
public class Proxy
extends Application
implements AutoCloseable, Runnable {

    private static final Random rng = new Random();

    // Flag used to stop certain periodic jobs from running during a reboot
    volatile boolean running = false;

    private final MCUEmulatorPort masterPort;
    private final MCUEmulatorPort extender1Port;
    private final MCUEmulatorPort extender2Port;

    private byte[] masterInitId;
    private byte[] extender1InitId;
    private byte[] extender2InitId;

    // Package-shared so EventHandlers can access it
    private final MCUEmulatorPort[] emulatorPorts;

    private final MCUEventHandler[] mcuEventHandlers;
    private final D8BEventHandler d8bEventHandler = new D8BEventHandler(this);

    private final Queue<Command> masterCommands = new ConcurrentLinkedQueue<>();
    private final Queue<Command> extender1Commands = new ConcurrentLinkedQueue<>();
    private final Queue<Command> extender2Commands = new ConcurrentLinkedQueue<>();

    // Models used to control physical feedback
    private final FadersModel fadersModel = new FadersModel(this);
    private final MetersModel metersModel = new MetersModel(this);

    // The CHANNEL/ASSIGNMENT indicator.
    public String assignment = "  ";

    private volatile BigInteger d8bSerialNumber;

    // Health stats about hearing from the console
    private Instant lastPong = null;
    long pongIntervalMillisSum = 0L;
    int pongIntervalCount = 0;

    private Instant lastHeartbeat1 = null;
    long heartbeat1IntervalMillisSum = 0L;
    int heartbeat1IntervalCount = 0;

    private Instant lastHeartbeat2 = null;
    long heartbeat2IntervalMillisSum = 0L;
    int heartbeat2IntervalCount = 0;


    /**
     * @param console - serial port to d8b console side
     * @param audio - serial port to d8b audio side
     * @param port1 - the port the host thinks is the Master
     * @param port2 - the port the host thinks is the first Extender
     * @param port3 - the port the host thinks is the second Extender
     */
    public Proxy(final ConsoleControlConnection console,
            final AudioControlConnection audio,
            final MCUEmulatorPort port1,
            final MCUEmulatorPort port2,
            final MCUEmulatorPort port3) {
        super(console, audio);

        this.masterPort = port1;
        this.extender1Port = port2;
        this.extender2Port = port3;
        this.emulatorPorts = new MCUEmulatorPort[] {
                port1,
                port2,
                port3
        };

        masterInitId = generateInitId();
        extender1InitId = generateInitId();
        extender2InitId = generateInitId();

        mcuEventHandlers = new MCUEventHandler[] {
                new MCUEventHandler(port1, 0, this),
                new MCUEventHandler(port2, 1, this),
                new MCUEventHandler(port3, 2, this)
        };

        // Now, wake up to the world
        masterPort.subscribe(masterCommands);
        extender1Port.subscribe(extender1Commands);
        extender2Port.subscribe(extender2Commands);
    }

    @Override
    public void run() {
        waitFor(new FullBoot(console, audio));
        waitFor(new Reset(console));
        consoleSignals.clear();
        console.send(new RequestSerialNumber());

        final Instant deadline = AppClock.now().plusSeconds(15);
        final BigInteger d8bSerialNumber;
        do {
            final Signal signal = consoleSignals.poll();
            if (signal != null && signal.type == SignalType.SerialNumber) {
                d8bSerialNumber = ((SerialNumber) signal).serialNumber;
                break;
            }
            if (deadline.isBefore(AppClock.now())) {
                throw new RuntimeException("D8B didn't report serial number");
            }
        } while (true);
        this.d8bSerialNumber = d8bSerialNumber;

        // Clear the queues -- only hear new messages
        masterCommands.clear();
        extender1Commands.clear();
        extender2Commands.clear();
        consoleSignals.clear();

        executor.scheduleAtFixedRate(metersModel,
                20,
                20,
                TimeUnit.MILLISECONDS);

        executor.scheduleAtFixedRate(fadersModel,
                20,
                20,
                TimeUnit.MILLISECONDS);

        executor.scheduleAtFixedRate(new DisplayRefresher(this),
                100,
                500,
                TimeUnit.MILLISECONDS);

        executor.scheduleAtFixedRate(new ConsolePinger(this),
                2,
                5,
                TimeUnit.SECONDS);

        this.virtualScreen.paint();

        running = true;

        // Announce we're awake
        this.emulatorPorts[0].send(new ReportAwake(DeviceType.Master, masterInitId));
        this.emulatorPorts[1].send(new ReportAwake(DeviceType.Extender, extender1InitId));
        this.emulatorPorts[2].send(new ReportAwake(DeviceType.Extender, extender2InitId));

        // Now listen and act
        while (!shutdown) {
            try {
                process(0, masterCommands.poll());
                process(1, extender1Commands.poll());
                process(2, extender2Commands.poll());
                process(consoleSignals.poll());
            } catch (RuntimeException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * Handle MCU message send to the specified virtual unit.
     * @param i 0 = master, 1 = 1st extender, 2 = 2nd extender, etc.
     * @param command
     */
    private void process(final int i, final Command command) {
        if (command == null) {
            return;
        }
        if (command instanceof SetSignalLevel) {
            //
        } else {
            System.out.println("Working on MCU command " + i + "::" + command);
        }
        typedDispatch(mcuEventHandlers[i], "dispatch", command);
    }

    /**
     * Handle d8b Signal.
     * @param signal
     */
    private void process(final Signal signal) {
        if (signal == null) {
            return;
        }

        typedDispatch(d8bEventHandler, "dispatch", signal);
    }


    @Override
    public void close() throws Exception {
        try {
            masterPort.unsubscribe(masterCommands);
            extender1Port.unsubscribe(extender1Commands);
            extender2Port.unsubscribe(extender2Commands);
        } finally {
            executor.shutdown();
        }
    }

    BigInteger getD8BSerialNumber() {
        return d8bSerialNumber;
    }

    /**
     * Send Commands to the d8b physical console.
     * @param commands
     */
    public void sendConsole(final com.adamdbradley.d8b.console.command.Command ... commands) {
        console.send(commands);
    }

    /**
     * Send Commands to the d8b physical console.
     * @param commands
     */
    public void sendConsole(final List<? extends com.adamdbradley.d8b.console.command.Command> commands) {
        console.send(commands);
    }

    public void reboot(final int i) {
        switch (i) {
        case 0:
            running = false;

            // Stop listening to anything
            console.unsubscribe(consoleSignals);
            masterPort.unsubscribe(masterCommands);
            extender1Port.unsubscribe(extender1Commands);
            extender2Port.unsubscribe(extender2Commands);

            // Purge all message backlogs
            consoleSignals.clear();
            masterCommands.clear();
            extender1Commands.clear();
            extender2Commands.clear();

            // Reset the console surface
            waitFor(new Reset(console));

            // Set D8B display features that aren't under MCU control
            restoreLocalD8B();

            masterInitId = generateInitId();

            // Re-subscribe all queues
            console.subscribe(consoleSignals);
            masterPort.subscribe(masterCommands);
            extender1Port.subscribe(extender1Commands);
            extender2Port.subscribe(extender2Commands);

            running = true;

            // Send the "I just woke up message"
            emulatorPorts[0].send(new ReportAwake(DeviceType.Master, masterInitId));
            return;

        case 1:
            extender1InitId = generateInitId();
            emulatorPorts[1].send(new ReportAwake(DeviceType.Extender, extender1InitId));
            return;

        case 2:
            extender2InitId = generateInitId();
            emulatorPorts[2].send(new ReportAwake(DeviceType.Extender, extender2InitId));
            return;

        default:
            throw new IllegalArgumentException("Don't know about " + i);
        }
    }


    /**
     * Set all d8b display features that aren't under MCU control.
     */
    private void restoreLocalD8B() {
        // TODO: Once I (ADB) have defined what this is and how to do it...
    }


    private static byte[] generateInitId() {
        final byte[] result = new byte[11];
        for (int i=0; i<11; i++) {
            result[i] = (byte) (rng.nextInt(127) + 1);
        }
        return result;
    }


    private final int VIEWPORT_WIDTH = 7 * 4;
    private final int SINGLE_VIRTUAL_WIDTH = 56;

    private final VirtualScreen virtualScreen = new VirtualScreen(console, 3, SINGLE_VIRTUAL_WIDTH, 2, VIEWPORT_WIDTH, 7);

    public void writeVirtualScreen(final int i, final WriteScreen command) {
        virtualScreen.write(i, command.row, command.column, command.string);
    }

    public void shiftVirtualScreenLeft() {
        virtualScreen.shiftLeft();
    }

    public void shiftVirtualScreenRight() {
        virtualScreen.shiftRight();
    }



    public void updateLevel(final Fader fader, final int value) {
        metersModel.updateLevel(fader, value);
    }


    public void d8bFaderMove(final Fader fader, final int position) {
        fadersModel.consoleMoved(fader, position);
    }

    public void commandFaderMove(final int i, final com.adamdbradley.mcu.console.Fader fader,
            final int value) {
        fadersModel.commandMove(i, fader, value);
    }

    public byte[] getInitId(final int i) {
        return new byte[][] { masterInitId, extender1InitId, extender2InitId } [i];
    }

    public void d8bPong() {
        final Instant now = AppClock.now();
        if (lastPong != null) {
            pongIntervalMillisSum += (now.toEpochMilli() - lastPong.toEpochMilli());
            pongIntervalCount++;
        }
        lastPong = now;
    }

    public void d8bHeartbeat1() {
        final Instant now = AppClock.now();
        if (lastHeartbeat1 != null) {
            heartbeat1IntervalMillisSum += (now.toEpochMilli() - lastHeartbeat1.toEpochMilli());
            heartbeat1IntervalCount++;
        }
        lastHeartbeat1 = AppClock.now();
    }

    public void d8bHeartbeat2() {
        final Instant now = AppClock.now();
        if (lastHeartbeat2 != null) {
            heartbeat2IntervalMillisSum += (now.toEpochMilli() - lastHeartbeat2.toEpochMilli());
            heartbeat2IntervalCount++;
        }
        lastHeartbeat2 = AppClock.now();
    }

    public void sendMasterEmulatorSignal(final com.adamdbradley.mcu.console.protocol.Signal signal) {
        sendEmulatorSignal(0, signal);
    }

    public void sendEmulatorSignal(final int signalPartition,
            final com.adamdbradley.mcu.console.protocol.Signal emulatedSignal) {
        emulatorPorts[signalPartition].send(emulatedSignal);
    }

    public void setSignalLevelMode(final com.adamdbradley.d8b.console.Fader fader,
            final SignalLevelDisplayMode mode) {
        metersModel.signalLevelMode[fader.ordinal()] = mode;
    }

}
