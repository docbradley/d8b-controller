package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.InvalidMidiDataException;

public class ChannelPressureConsoleMessage
extends Message {

    public ChannelPressureConsoleMessage(final byte channel, final byte value) {
        super(build(channel, value));
    }

    private static ChannelPressureMidiMessage build(final byte channel, final byte value) {
        try {
            return new ChannelPressureMidiMessage(channel, value);
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
