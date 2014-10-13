package com.adamdbradley.d8b.console.signal;

/**
 * Informational message about the state of the console.
 */
public class Pong extends Signal {

    private final int parameter;

    public Pong(final String command) {
        super(SignalType.Pong);
        this.parameter = Integer.parseInt(command, 16);
    }

    @Override
    public String toString() {
        return timestamp + " Pong " + Integer.toBinaryString(parameter);
    }

}
