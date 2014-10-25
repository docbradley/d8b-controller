package com.adamdbradley.d8b.console.signal;

public class Heartbeat1 extends UnparameterizedSignal {

    public Heartbeat1(final String command) {
        super(SignalType.Heartbeat1, command);
    }

    @Override
    public String toString() {
        return timestamp + " Heartbeat1";
    }

}
