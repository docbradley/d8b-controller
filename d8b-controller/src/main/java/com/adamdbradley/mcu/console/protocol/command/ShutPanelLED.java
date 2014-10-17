package com.adamdbradley.mcu.console.protocol.command;

import com.adamdbradley.mcu.console.PanelButton;
import com.adamdbradley.mcu.console.PanelLED;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.NoteOnMessageBase;
import com.adamdbradley.mcu.console.protocol.signal.ButtonMessage;

public class ShutPanelLED
extends NoteOnMessageBase
implements Command {

    public ShutPanelLED(final PanelLED led) {
        super((byte) 0x0, encode(led), (byte) 0x0);
    }

    private static byte encode(final PanelLED led) {
        // LEDs and buttons have the same encodings.
        // Since PanelButtons are a superset of PanelLEDs, put the mapping there.
        return ButtonMessage.encode(PanelButton.valueOf(led.name()));
    }

}