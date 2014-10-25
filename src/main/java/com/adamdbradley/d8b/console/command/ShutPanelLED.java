package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.PanelLED;

/**
 * Turn off a particular Panel LED.
 * See {@link LightPanelLED}.
 * For MeterLEDs, see {@link ShutPanelLED}.
 */
public class ShutPanelLED extends PanelLEDCommand {

    public ShutPanelLED(final PanelLED led) {
        super(CommandType.ShutPanelLED, led);
    }

}
