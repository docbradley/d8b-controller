package com.adamdbradley.d8b.mcuproxy;

import java.time.Instant;

import com.adamdbradley.d8b.AppClock;
import com.adamdbradley.d8b.console.Fader;
import com.adamdbradley.d8b.console.MeterLEDChannel;
import com.adamdbradley.d8b.console.MeterLEDNumber;
import com.adamdbradley.d8b.console.command.MeterLEDHelper;
import com.adamdbradley.mcu.console.SignalLevelDisplayMode;

class MetersModel implements Runnable {

    private final Proxy proxy;

    /**
     * @param proxy
     */
    MetersModel(final Proxy proxy) {
        this.proxy = proxy;
    }

    final SignalLevelDisplayMode signalLevelMode[];
    {
        signalLevelMode = new SignalLevelDisplayMode[3 * 8];
        for (int i=0; i<signalLevelMode.length; i++) {
            signalLevelMode[i] = SignalLevelDisplayMode.METER_AND_PEAK; // A sensible default
        }
    };
    final MeterLEDNumber[] currentLevel = new MeterLEDNumber[24]; // All start as null
    final MeterLEDNumber[] currentPeak = new MeterLEDNumber[24]; // All start as null
    final Instant[] peakLastReached = new Instant[24]; // All start as null

    public void updateLevel(final Fader fader, final int value) {
        int position = fader.ordinal();

        final SignalLevelDisplayMode mode = signalLevelMode[position];

        if (mode.indicator()) {
            // THINKME: use "write" LED as indicator?
        }

        // MCU range is [0 ... 15] but D8B range is OFF + [0 ... 12],
        // so we need to do a lossy collapse.
        // MCU 0 -> OFF
        // MCU 1,2 -> 0
        // MCU 3,4 -> 1
        // MCU [5..F] -> [2..C]
        final MeterLEDNumber newLevel;
        if (value == 0) {
            newLevel = null;
        } else if (value < 5) {
            newLevel = MeterLEDNumber.values()[((value + 1) / 2)];
        } else {
            newLevel = MeterLEDNumber.values()[value - 3];
        }

        if (newLevel == null) {
            return;
        } else if (currentLevel[position] == null) {
            currentLevel[position] = newLevel;
        } else if (currentLevel[position].ordinal() < newLevel.ordinal()) {
            currentLevel[position] = newLevel;
        }

        if (currentPeak[position] == null
                || currentPeak[position].ordinal() <= newLevel.ordinal()) {
            currentPeak[position] = newLevel;
            peakLastReached[position] = AppClock.now();
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
        for (int i=0; i<24; i++) {
            final SignalLevelDisplayMode mode = signalLevelMode[i];
            final MeterLEDNumber level = currentLevel[i];
            final MeterLEDNumber peak = currentPeak[i];

            // Render it
            final MeterLEDHelper.MeterLEDMask mask = new MeterLEDHelper.MeterLEDMask();
            if (mode.meter() || mode.peak()) {

                if (mode.meter()) {
                    mask.fillTo(level);
                }

                if (mode.peak() && peak != null) {
                    mask.set(peak);
                }
            }
            this.proxy.sendConsole(MeterLEDHelper.buildMeterBitmaskCommands(MeterLEDChannel.values()[i], mask));

            // Now update current level
            if (level != null) {
                if (level.ordinal() == 0) {
                    currentLevel[i] = null;
                } else {
                    currentLevel[i] = MeterLEDNumber.values()[level.ordinal() - 1];
                }
            }

            if (peak != null
                    && peakLastReached[i].plusMillis(750).isBefore(AppClock.now())) {
                if (peak.ordinal() == 0) {
                    currentPeak[i] = null;
                } else {
                    currentPeak[i] = MeterLEDNumber.values()[peak.ordinal() - 1];
                }
                peakLastReached[i] = peakLastReached[i].plusMillis(20);
            }

        }
    }
}