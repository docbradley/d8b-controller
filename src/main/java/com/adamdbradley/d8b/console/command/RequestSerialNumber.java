package com.adamdbradley.d8b.console.command;

/**
 * Light up all LEDs (Panel and Meter).
 * See also {@link ShutAllLEDs}.
 */
public class RequestSerialNumber extends UnparameterizedCommand {

    public RequestSerialNumber() {
        super(CommandType.RequestSerialNumber);
    }

}
