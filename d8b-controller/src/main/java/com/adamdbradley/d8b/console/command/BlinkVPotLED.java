package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.VPotLED;

/**
 * Illuminate a single console panel LED.
 * See also {@link ShutPanelLED}.
 * For channel level meters, use {@link LightMeterLED}.
 */
public class BlinkVPotLED extends VPotLEDCommand {

    public BlinkVPotLED(final VPotLED led) {
        super(CommandType.BlinkPanelLED, led);
    }

}
