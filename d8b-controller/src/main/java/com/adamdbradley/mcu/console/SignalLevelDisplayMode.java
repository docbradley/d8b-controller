package com.adamdbradley.mcu.console;

/**
 * "INDICATOR" is a "signal present" indicator
 * (e.g., the channel LED on the MCU).
 * "METER" is a scaled level indicator
 * (e.g., the horizontal meters on the lower line of
 * the MCU screen or the vertical LED strips on the d8b).
 * "PEAK" holds the peak level on the "METER" display
 * (either as a distinct symbol on the MCU screen or by
 * simply holding the peak LED on d8b's vertical LED strips).
 */
public enum SignalLevelDisplayMode {

    OFF,
    INDICATOR,
    METER,
    INDICATOR_AND_METER,
    PEAK,
    INDICATOR_AND_PEAK,
    METER_AND_PEAK,
    INDICATOR_AND_METER_AND_PEAK

}
