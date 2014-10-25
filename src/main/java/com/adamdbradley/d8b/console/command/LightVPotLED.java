package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.VPotLED;

/**
 * Illuminate a single console panel LED.
 * See also {@link ShutPanelLED}.
 * For channel level meters, use {@link LightMeterLED}.
 */
public class LightVPotLED extends VPotLEDCommand {

    public LightVPotLED(final VPotLED led) {
        super(CommandType.LightPanelLED, led);
    }

}
