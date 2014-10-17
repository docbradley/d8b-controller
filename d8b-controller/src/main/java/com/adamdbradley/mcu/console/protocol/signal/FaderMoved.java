package com.adamdbradley.mcu.console.protocol.signal;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.protocol.PitchBendMessageBase;
import com.adamdbradley.mcu.console.protocol.Signal;

public class FaderMoved
extends PitchBendMessageBase
implements Signal {

    public final Fader fader;
    public final int value;

    /**
     * @param fader
     * @param value range is 0 - 1023 (0x000 - 0x3FF)
     * @throws InvalidMidiDataException 
     */
    public FaderMoved(final Fader fader, final int value) {
        super((byte) fader.ordinal(), value << 4);

        this.fader = fader;
        this.value = value;
    }

}
