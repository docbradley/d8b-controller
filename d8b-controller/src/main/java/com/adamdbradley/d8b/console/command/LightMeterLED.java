package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.MeterLEDChannel;
import com.adamdbradley.d8b.console.MeterLEDNumber;

/**
 * Illuminate a single LED in a channel meter.
 * See also {@link ShutMeterLED}.
 * For panel LEDs, use {@link LightPanelLED}.
 */
public class LightMeterLED extends MeterLEDCommand {

    public LightMeterLED(final MeterLEDChannel meter, final MeterLEDNumber ledNumber) {
        super(CommandType.LightMeterLED, meter, ledNumber);
    }

}
