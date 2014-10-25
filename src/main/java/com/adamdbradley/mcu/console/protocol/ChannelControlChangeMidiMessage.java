package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class ChannelControlChangeMidiMessage extends ShortMessage {

    public ChannelControlChangeMidiMessage(final byte channel,
            final byte controllerNumber,
            final byte value)
                    throws InvalidMidiDataException {
        super(0b10110000 | channel, controllerNumber, value);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(this);
    }

}
