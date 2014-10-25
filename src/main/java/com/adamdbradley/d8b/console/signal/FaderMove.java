package com.adamdbradley.d8b.console.signal;

import com.adamdbradley.d8b.console.Fader;

public class FaderMove extends Signal {

    public final Fader fader;

    /**
     * Values range 0 - 255.
     */
    public final int position;

    public FaderMove(final String command) {
        super(SignalType.FaderMove);
        if (command.length() != 4) {
            throw new IllegalArgumentException("Couldn't parse [" + command + "]");
        }
        final int faderNumber = Integer.parseInt(command.substring(0, 2), 16);
        this.fader = Fader.values()[faderNumber];
        this.position = Integer.parseInt(command.substring(2), 16);
    }

    @Override
    public String toString() {
        return timestamp + " FaderMove " + fader + " " + position;
    }
}
