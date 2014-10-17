package com.adamdbradley.mcu.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

public class ChannelPressureConsoleMessage
implements Message {

    private final ChannelPressureMidiMessage message;

    public ChannelPressureConsoleMessage(final byte channel, final byte value) {
        try {
            message = new ChannelPressureMidiMessage(channel, value);
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
