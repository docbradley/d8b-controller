package com.adamdbradley.mcu.console.protocol.command;

import com.adamdbradley.mcu.console.PanelLED;
import com.adamdbradley.mcu.console.protocol.Command;

public class LightPanelLED
extends PanelLEDCommandBase
implements Command {

    public LightPanelLED(final PanelLED led) {
        super(led, true);
    }

}
