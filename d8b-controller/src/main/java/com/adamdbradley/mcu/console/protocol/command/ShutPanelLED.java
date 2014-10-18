package com.adamdbradley.mcu.console.protocol.command;

import com.adamdbradley.mcu.console.PanelLED;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.NoteOnMessageBase;

public class ShutPanelLED
extends NoteOnMessageBase
implements Command {

    public final PanelLED led;

    public ShutPanelLED(final PanelLED led) {
        super((byte) 0x0, LightPanelLED.encode(led), (byte) 0x0);

        this.led = led;
    }

}
