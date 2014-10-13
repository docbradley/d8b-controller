package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.VPotLED;

/**
 * Turn off a single VPot LED segment.
 * See also {@link LightVPotLED}.
 */
public class ShutVPotLED extends VPotLEDCommand {

    public ShutVPotLED(final VPotLED led) {
        super(CommandType.ShutPanelLED, led);
    }

}
