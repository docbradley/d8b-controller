package com.adamdbradley.mcu.console.protocol.command;

import com.adamdbradley.mcu.console.PanelButton;
import com.adamdbradley.mcu.console.PanelLED;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.NoteOnConsoleMessage;
import com.adamdbradley.mcu.console.protocol.signal.ButtonMessage;

public abstract class PanelLEDCommandBase
extends NoteOnConsoleMessage
implements Command {

    public final PanelLED led;

    public PanelLEDCommandBase(final PanelLED led, final boolean light) {
        super((byte) 0x0, encode(led),
                light ? ((byte) 0x7F) : ((byte) 0x00));

        this.led = led;
    }

    static byte encode(final PanelLED led) {
        if (led == PanelLED.Timecode_SMPTE || led == PanelLED.Timecode_BEATS) {
            throw new IllegalArgumentException();
        } else {
            // When they coincide, LEDs and buttons have the same encodings.
            // Since PanelButtons are a superset of PanelLEDs, put the mapping there.
            return ButtonMessage.encode(PanelButton.valueOf(led.name()));
        }
    }

}
