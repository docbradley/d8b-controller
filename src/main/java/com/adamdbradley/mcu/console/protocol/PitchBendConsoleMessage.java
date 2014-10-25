package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.InvalidMidiDataException;

public class PitchBendConsoleMessage
extends Message {

    /**
     * @param channel
     * @param value range is 14 bits (0 - 16383)
     */
    public PitchBendConsoleMessage(final byte channel, final int value) {
        super(build(channel, value));
    }

    private static PitchBendMidiMessage build(final byte channel, final int value) {
        try {
            return new PitchBendMidiMessage(channel, value);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

}
