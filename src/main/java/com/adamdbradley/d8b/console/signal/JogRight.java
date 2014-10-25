package com.adamdbradley.d8b.console.signal;

public class JogRight extends UnparameterizedSignal {

    public JogRight(final String command) {
        super(SignalType.JogLeft, command);
    }

    @Override
    public String toString() {
        return timestamp + " JogRight";
    }

}
