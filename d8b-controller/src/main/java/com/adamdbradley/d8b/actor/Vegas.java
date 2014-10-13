package com.adamdbradley.d8b.actor;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.adamdbradley.d8b.AppClock;
import com.adamdbradley.d8b.console.Fader;
import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.d8b.console.MeterLEDChannel;
import com.adamdbradley.d8b.console.MeterLEDNumber;
import com.adamdbradley.d8b.console.PanelLED;
import com.adamdbradley.d8b.console.VPot;
import com.adamdbradley.d8b.console.VPotLED;
import com.adamdbradley.d8b.console.VPotLEDPosition;
import com.adamdbradley.d8b.console.command.Command;
import com.adamdbradley.d8b.console.command.LightMeterLED;
import com.adamdbradley.d8b.console.command.LightPanelLED;
import com.adamdbradley.d8b.console.command.LightVPotLED;
import com.adamdbradley.d8b.console.command.MoveFader;
import com.adamdbradley.d8b.console.command.ScreenHelper;
import com.adamdbradley.d8b.console.command.SetChannelNumber;
import com.adamdbradley.d8b.console.command.ShutMeterLED;
import com.adamdbradley.d8b.console.command.ShutPanelLED;
import com.adamdbradley.d8b.console.command.ShutVPotLED;
import com.adamdbradley.d8b.console.command.TimecodeHelper;

public class Vegas
extends ConsoleActor
implements Callable<Void> {

    private static final Random rng = new Random();

    private final Duration duration;

    private ScheduledExecutorService executor;
    private Instant endAfter;

    public Vegas(final ConsoleControlConnection console, final Duration duration) {
        super(console, null);
        this.duration = duration;
    }

    private final Runnable screenUpdater = new Runnable() {
        private int phase = 0;
        private static final String NOISE = "Shall we play a game? How about GLOBAL THERMONUCLEAR WAR? ";
        private static final String SOURCE = NOISE + NOISE;

        @Override
        public void run() {
            final int offset = phase;
            console.send(ScreenHelper.buildDrawScreenCommands(0, 0, SOURCE.substring(offset, 40 + offset)));
            console.send(ScreenHelper.buildDrawScreenCommands(1, 0, SOURCE.substring(offset + 22, 40 + offset + 22)));
            phase = (phase + 1) % 4;
        }
    };

    private final Runnable metersUpdater = new Runnable() {
        private final int STRIDE = 6;
        private int phase = 0;
        @Override
        public void run() {
            final List<Command> commands = new ArrayList<>(MeterLEDChannel.values().length * MeterLEDNumber.values().length);
            for (final MeterLEDChannel meter: MeterLEDChannel.values()) {
                final int channelPhase = (phase + meter.ordinal()) % STRIDE;
                for (final MeterLEDNumber led: MeterLEDNumber.values()) {
                    if (channelPhase == led.ordinal() % STRIDE) {
                        commands.add(new LightMeterLED(meter, led));
                    } else {
                        commands.add(new ShutMeterLED(meter, led));
                    }
                }
            }
            console.send(commands);
            phase++;
        }
    };

    private final Runnable channelUpdater = new Runnable() {
        private int phase = 0;
        @Override
        public void run() {
            console.send(new SetChannelNumber((phase * 7) % 100));
            phase++;
        }
    };

    private final Runnable timecodeUpdater = new Runnable() {
        @Override
        public void run() {
            StringBuilder launchCode = new StringBuilder();
            for (int i=0; i<12; i++) {
                launchCode.append(Integer.toString(rng.nextInt(10)));
            }
            console.send(TimecodeHelper.buildRedrawTimecodeCommands(launchCode.toString()));
        }
    };

    private final Runnable panelUpdater = new Runnable() {
        private final String[] NAMES = { "RecReadyProxy", "Assign", "Write", "GreenDot", "RedDot", "Select", "Solo", "Mute" };
        private int phase = 0;
        @Override
        public void run() {
            final List<Command> commands = new ArrayList<>(Fader.values().length * NAMES.length * 2);
            for (final PanelLED panelLED: PanelLED.values()) {
                if (panelLED.name().startsWith("ch")) {
                    if (panelLED.name().endsWith(NAMES[phase % NAMES.length])) {
                        commands.add(new LightPanelLED(panelLED));
                    } else {
                        commands.add(new ShutPanelLED(panelLED));
                    }
                }
            }
            console.send(commands);
            phase++;
        }
    };

    private final Runnable vpotsUpdater = new Runnable() {
        private int phase = 0;
        @Override
        public void run() {
            final List<Command> commands = new ArrayList<>(VPot.values().length * 2);
            for (final VPot vpot: VPot.values()) {
                int oldPhase = (phase + vpot.ordinal()) % VPotLEDPosition.values().length;
                int newPhase = (phase + 1 + vpot.ordinal()) % VPotLEDPosition.values().length;
                commands.add(new ShutVPotLED(new VPotLED(vpot, VPotLEDPosition.values()[oldPhase])));
                commands.add(new LightVPotLED(new VPotLED(vpot, VPotLEDPosition.values()[newPhase])));
            }
            console.send(commands);
            phase++;
        }
    };

    private final Runnable fadersUpdater = new Runnable() {
        private int phase = 0;
        @Override
        public void run() {
            final double wavePhase = phase * 0.2;
            final double warmUp = Math.min(1.0, (1.0 * phase / 80.0));
            final List<Command> commands = new ArrayList<>(VPot.values().length * 2);
            for (final Fader channel: Fader.values()) {
                final double channelPosition = (Math.PI * channel.ordinal() / 6.0) + wavePhase;
                final double rawValue = Math.sin(channelPosition);
                final double scaledValue = (rawValue * 127.9) + 128.0;
                final double warmedValue = warmUp * scaledValue;
                final int discreteValue = (int) warmedValue;
                commands.add(new MoveFader(channel, discreteValue));
            }
            console.send(commands);
            phase++;
        }
    };

    private final Runnable shutdown = new Runnable() {
        public void run() {
            if (AppClock.now().isAfter(endAfter)) {
                executor.shutdown();
                executor = null;
            }
        }
    };

    @Override
    public Void call() throws InterruptedException {
        this.executor = Executors.newScheduledThreadPool(4);

        this.endAfter = AppClock.now().plus(duration);

        console.send(new ShutPanelLED(PanelLED.dcapanel_RudeSolo));

        executor.scheduleAtFixedRate(screenUpdater, 5000, 750, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(metersUpdater, 1000, 25, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(channelUpdater, 100, 125, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(timecodeUpdater, 200, 100, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(panelUpdater, 750, 2000, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(vpotsUpdater, 500, 60, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(fadersUpdater, 1000, 100, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(shutdown, 5, 5, TimeUnit.SECONDS);

        return null;
    }

}
