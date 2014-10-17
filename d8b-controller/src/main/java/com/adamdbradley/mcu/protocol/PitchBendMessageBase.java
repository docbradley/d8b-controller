package com.adamdbradley.mcu.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

public class PitchBendMessageBase
implements Message {

    private final PitchBendMessage message;

    /**
     * @param channel
     * @param value range is 14 bits (0 - 16383)
     */
    public PitchBendMessageBase(final byte channel, final int value) {
        try {
            message = new PitchBendMessage(channel, value);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public MidiMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(message);
    }

}
