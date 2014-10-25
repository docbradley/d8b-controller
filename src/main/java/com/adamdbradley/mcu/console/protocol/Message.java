package com.adamdbradley.mcu.console.protocol;

import java.util.Arrays;

import javax.sound.midi.MidiMessage;

/**
 * Immutable message object meant to instruct a control surface
 * to take some action.
 */
public abstract class Message {

    protected final MidiMessage message;

    protected Message(final MidiMessage message) {
        this.message = message;
    }

    public final MidiMessage getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Message)) {
            return false;
        }
        return Arrays.equals(message.getMessage(),
                ((Message) other).message.getMessage());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(message);
    }

}
