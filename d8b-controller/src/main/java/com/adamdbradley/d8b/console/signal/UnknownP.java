package com.adamdbradley.d8b.console.signal;

/**
 * Apparently an informational message about the state of the console.
 */
public class UnknownP extends Signal {

    private final int parameter;

    public UnknownP(final String command) {
        super(SignalType.UnknownP);
        this.parameter = Integer.parseInt(command, 16);
    }

    @Override
    public String toString() {
        return timestamp + " UnknownP " + Integer.toBinaryString(parameter);
    }

}
