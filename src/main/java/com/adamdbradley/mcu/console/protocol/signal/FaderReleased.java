package com.adamdbradley.mcu.console.protocol.signal;

import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.protocol.NoteOnConsoleMessage;
import com.adamdbradley.mcu.console.protocol.Signal;

public class FaderReleased
extends NoteOnConsoleMessage
implements Signal {

    public final Fader fader;

    public FaderReleased(Fader fader) {
        super((byte) 0x00,
                (byte) (0x68 + fader.ordinal()),
                (byte) 0x00);

        this.fader = fader;
    }

}
