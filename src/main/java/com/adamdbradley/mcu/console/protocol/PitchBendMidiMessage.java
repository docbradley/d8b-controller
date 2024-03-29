package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class PitchBendMidiMessage extends ShortMessage {

    /**
     * @param channel
     * @param value range is 0 - 16383 (0x000 - 0x3FFF).
     * @throws InvalidMidiDataException 
     */
    public PitchBendMidiMessage(final byte channel, final int value) throws InvalidMidiDataException {
        super((byte) (0xE0 | channel),
                value & 0x7F,
                value >> 7);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(this);
    }

}
