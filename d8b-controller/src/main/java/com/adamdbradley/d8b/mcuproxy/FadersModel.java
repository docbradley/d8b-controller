package com.adamdbradley.d8b.mcuproxy;

import java.time.Instant;

import com.adamdbradley.d8b.AppClock;
import com.adamdbradley.d8b.console.Fader;
import com.adamdbradley.d8b.console.command.MoveFader;
import com.adamdbradley.mcu.console.protocol.signal.FaderReleased;

class FadersModel implements Runnable {

    private final Proxy proxy;

    /**
     * @param proxy
     */
    FadersModel(final Proxy proxy) {
        this.proxy = proxy;
    }

    private final Object[] faderMonitors = new Object[25];
    {
        for (int i=0; i<25; i++) {
            faderMonitors[i] = new Object();
        }
    }
    private final Instant[] faderLastMovedFromConsole = new Instant[25];
    private final int[] faderLastPhysicalPosition = new int[25];  // 0-1023 scale
    private final int[] faderLastCommandedPosition = new int[25]; // 0-1023 scale

    public void commandMove(final int i,
            final com.adamdbradley.mcu.console.Fader fader,
            final int value) {
        final Fader consoleFader = ControllerMaps.map(i, fader);
        synchronized(faderMonitors[consoleFader.ordinal()]) {
            faderLastCommandedPosition[consoleFader.ordinal()] = value;
            moveFaderIfAllowed(consoleFader.ordinal());
        }
    }

    public void consoleMoved(final Fader fader, final int position) {
        final int mcuValue = (position * 1023) / 255;

        synchronized(faderMonitors[fader.ordinal()]) {
            final com.adamdbradley.mcu.console.Fader mcuFader;
            if (fader == Fader.Master) {
                mcuFader = com.adamdbradley.mcu.console.Fader.Master;

                if (faderLastMovedFromConsole[fader.ordinal()] == null) {
                    proxy.sendMasterEmulatorSignal(new com.adamdbradley.mcu.console.protocol.signal
                            .FaderTouched(com.adamdbradley.mcu.console.Fader.Master));
                }

                System.err.println("Telling MCU master is " + position + "::" + mcuValue);

                proxy.sendMasterEmulatorSignal(new com.adamdbradley.mcu.console.protocol.signal
                        .FaderMoved(mcuFader, mcuValue));
            } else {
                final int bank = 2 - (fader.ordinal() / 8);
                mcuFader = com.adamdbradley.mcu.console.Fader.values()[fader.ordinal() % 8];

                if (faderLastMovedFromConsole[fader.ordinal()] == null) {
                    proxy.sendEmulatorSignal(bank, new com.adamdbradley.mcu.console.protocol.signal
                            .FaderTouched(mcuFader));
                }

                System.err.println("Telling MCU " + bank + ":" + mcuFader + " is " + position + "::" + mcuValue);

                proxy.sendEmulatorSignal(bank, new com.adamdbradley.mcu.console.protocol.signal
                        .FaderMoved(mcuFader, mcuValue));
            }
            faderLastMovedFromConsole[fader.ordinal()] = AppClock.now();
            faderLastPhysicalPosition[fader.ordinal()] = mcuValue;
        }
    }

    public void run() {
        if (!this.proxy.running) {
            return;
        }

        try {
            execute();
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        }
    }

    private void execute() {
        for (int i=0; i<25; i++) {
            synchronized(faderMonitors[i]) {
                // If the fader hasn't moved in at least 1000ms, assume it's been released
                if (faderLastMovedFromConsole[i] != null
                        && faderLastMovedFromConsole[i].plusMillis(1000).isBefore(AppClock.now())) {
                    if (i<24) {
                        proxy.sendEmulatorSignal(2 - (i/8), new FaderReleased(com.adamdbradley.mcu.console.Fader.values()[i%8]));
                    } else {
                        proxy.sendEmulatorSignal(0, new FaderReleased(com.adamdbradley.mcu.console.Fader.Master));
                    }
                    faderLastMovedFromConsole[i] = null;
                }

                moveFaderIfAllowed(i);
            }
        }
    }

    private void moveFaderIfAllowed(final int i) {
        // If the fader is in a "released" state and the last commanded value
        // is far enough away from the last physical value, request the console
        // move the physical fader.
        if (faderLastMovedFromConsole[i] == null
                && (Math.abs(faderLastCommandedPosition[i] - faderLastPhysicalPosition[i]) >= 8)) {
            final int d8bFaderValue = (faderLastCommandedPosition[i] * 255) / 1023;
            proxy.sendConsole(new MoveFader(Fader.values()[i], d8bFaderValue));
            faderLastPhysicalPosition[i] = faderLastCommandedPosition[i];
        }
    }

}
