package com.adamdbradley.mcu.console.protocol.command;

import com.adamdbradley.mcu.console.PanelLED;
import com.adamdbradley.mcu.console.protocol.Command;

public class ShutPanelLED
extends PanelLEDCommandBase
implements Command {

    public ShutPanelLED(final PanelLED led) {
        super(led, false);
    }

}
