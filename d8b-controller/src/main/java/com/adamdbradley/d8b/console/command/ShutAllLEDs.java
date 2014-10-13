package com.adamdbradley.d8b.console.command;

/**
 * Turn off all LEDs (Panel, VPot and Meter).
 * See also {@link LightAllLEDs}.
 */
public class ShutAllLEDs extends UnparameterizedCommand {

    public ShutAllLEDs() {
        super(CommandType.ShutAllLEDs);
    }

}
