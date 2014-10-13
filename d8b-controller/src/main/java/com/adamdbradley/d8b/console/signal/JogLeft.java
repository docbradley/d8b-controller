package com.adamdbradley.d8b.console.signal;

public class JogLeft extends UnparameterizedSignal {

    public JogLeft(final String command) {
        super(SignalType.JogLeft, command);
    }

    @Override
    public String toString() {
        return timestamp + " JogLeft";
    }

}
