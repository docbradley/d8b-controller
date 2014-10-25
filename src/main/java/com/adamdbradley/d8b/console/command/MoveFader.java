package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.Fader;

/**
 * Move a particular fader to a particular position.
 */
public class MoveFader extends Command {

    private final Fader fader;
    private final int value;

    public MoveFader(final Fader fader, final int value) {
        super(CommandType.MoveFader);
        this.fader = fader;
        if (value < 0 || value > 255) {
            throw new RuntimeException("Fader value out of range: " + value);
        }
        this.value = value;
    }

    @Override
    public String serializeParameters() {
        return toTwoDigitHexString(fader.ordinal())
                + toTwoDigitHexString(value);
    }

}
