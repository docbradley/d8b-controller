package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.InvalidMidiDataException;

public class ChannelControlChangeConsoleMessage
extends Message {

    public ChannelControlChangeConsoleMessage(final byte channel, final byte controllerNumber, final byte value) {
        super(build(channel, controllerNumber, value));
    }

    private static final ChannelControlChangeMidiMessage build(final byte channel,
            final byte controllerNumber,
            final byte value) {
        try {
            return new ChannelControlChangeMidiMessage(channel, controllerNumber, value);
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
