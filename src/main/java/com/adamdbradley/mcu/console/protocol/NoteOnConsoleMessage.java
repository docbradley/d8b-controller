package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.InvalidMidiDataException;

public class NoteOnConsoleMessage
extends Message {

    public NoteOnConsoleMessage(final byte channel, final byte note, final byte velocity) {
        super(build(channel, note, velocity));
    }

    private static NoteOnMidiMessage build(final byte channel,
            final byte note,
            final byte velocity) {
        try {
            return new NoteOnMidiMessage(channel, note, velocity);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(message);
    }

}
