package com.adamdbradley.mcu.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

public class NoteOnMessageBase {

    private final NoteOnMessage message;

    public NoteOnMessageBase(final byte channel, final byte note, final byte velocity) {
        try {
            message = new NoteOnMessage(channel, note, velocity);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

    public MidiMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(message);
    }

}
