package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.MidiMessage;

/**
 * Immutable message object meant to instruct a control surface
 * to take some action.
 */
public interface Command {

    MidiMessage getMessage();

}
