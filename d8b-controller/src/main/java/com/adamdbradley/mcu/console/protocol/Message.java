package com.adamdbradley.mcu.protocol;

import javax.sound.midi.MidiMessage;

/**
 * Immutable message object meant to instruct a control surface
 * to take some action.
 */
public interface Message {

    public MidiMessage getMessage();

}
