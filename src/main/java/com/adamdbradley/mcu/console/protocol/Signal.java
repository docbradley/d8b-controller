package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.MidiMessage;

/**
 * Immutable message object meant to indicate something has
 * happened to a control surface.
 */
public interface Signal {

    MidiMessage getMessage();

}
