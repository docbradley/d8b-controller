package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

public class ChannelControlChangeConsoleMessage
implements Message {

    private final ChannelControlChangeMidiMessage message;

    public ChannelControlChangeConsoleMessage(final byte channel, final byte controllerNumber, final byte value) {
        try {
            message = new ChannelControlChangeMidiMessage(channel, controllerNumber, value);
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
