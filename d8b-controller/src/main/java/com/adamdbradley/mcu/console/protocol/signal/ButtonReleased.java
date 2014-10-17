package com.adamdbradley.mcu.console.protocol.signal;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.PanelButton;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ButtonReleased
extends ButtonMessage
implements Signal {

    public ButtonReleased(PanelButton button)
            throws InvalidMidiDataException {
        super(button, false);
    }

}
