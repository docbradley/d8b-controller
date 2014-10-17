package com.adamdbradley.mcu.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class ChannelPressureMidiMessage extends ShortMessage {

    public ChannelPressureMidiMessage(final byte channel, final byte value)
            throws InvalidMidiDataException {
        super(0b11010000 | channel, value, -1 /* ignored */);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(this);
    }

}
