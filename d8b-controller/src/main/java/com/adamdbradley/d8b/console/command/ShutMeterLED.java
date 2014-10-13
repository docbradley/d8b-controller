package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.MeterLEDChannel;
import com.adamdbradley.d8b.console.MeterLEDNumber;

/**
 * Shut off a particular MeterLED on a particular channel.
 * See also {@link LightMeterLED}.
 * For Panel LEDs, see {@link ShutPanelLED}.
 */
public class ShutMeterLED extends MeterLEDCommand {

    /**
     * @param meter 0-23 (channel), 24-25 (master L/R)
     * @param ledNumber 0-11 (0-7 green, 8-10 yellow, 11 red)
     */
    public ShutMeterLED(final MeterLEDChannel meter,
            final MeterLEDNumber ledNumber) {
        super(CommandType.ShutMeterLED, meter, ledNumber);
    }

}
