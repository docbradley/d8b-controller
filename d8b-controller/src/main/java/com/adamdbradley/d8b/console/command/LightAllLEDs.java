package com.adamdbradley.d8b.console.command;

/**
 * Light up all LEDs (Panel, VPot, and Meter).
 * See also {@link ShutAllLEDs}.
 */
public class LightAllLEDs extends UnparameterizedCommand {

    public LightAllLEDs() {
        super(CommandType.LightAllLEDs);
    }

}
