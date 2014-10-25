package com.adamdbradley.d8b.console.signal;

public class Heartbeat2 extends UnparameterizedSignal {

    public Heartbeat2(final String command) {
        super(SignalType.Heartbeat2, command);
    }

    @Override
    public String toString() {
        return timestamp + " Heartbeat2";
    }

}
