package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.PanelLED;

/**
 * Illuminate a single console panel LED.
 * See also {@link ShutPanelLED}.
 * For channel level meters, use {@link LightMeterLED}.
 */
public class LightPanelLED extends PanelLEDCommand {

    public LightPanelLED(final PanelLED led) {
        super(CommandType.LightPanelLED, led);
    }

}
