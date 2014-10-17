package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class NoteOnMessage extends ShortMessage {

    public NoteOnMessage(final byte channel, final byte note, final byte velocity)
            throws InvalidMidiDataException {
        super(0b10010000 | channel, note, velocity);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(this);
    }

}
