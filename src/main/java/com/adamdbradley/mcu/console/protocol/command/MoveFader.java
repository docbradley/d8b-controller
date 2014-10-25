package com.adamdbradley.mcu.console.protocol.command;

import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.PitchBendConsoleMessage;

/**
 * Range 0 - 1023
 */
public class MoveFader
extends PitchBendConsoleMessage
implements Command {

    public final Fader fader;
    public final int value;

    public MoveFader(final Fader fader, final int value) {
        super((byte) fader.ordinal(), value << 4);

        if (value < 0 || value > 1023) {
            throw new IllegalArgumentException();
        }

        this.fader = fader;
        this.value = value;
    }

}
