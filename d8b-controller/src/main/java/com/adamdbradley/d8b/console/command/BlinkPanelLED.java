package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.PanelLED;

/**
 * Tell an LED to blink  (e.g., Solo and Record)
 */
public class BlinkPanelLED extends PanelLEDCommand {

    public BlinkPanelLED(final PanelLED led) {
        super(CommandType.BlinkPanelLED, led);
    }

}
