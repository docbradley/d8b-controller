package com.adamdbradley.d8b.console.signal;

abstract class UnparameterizedSignal extends Signal {

    UnparameterizedSignal(final SignalType signalType, final String command) {
        super(signalType);
        if (!command.isEmpty()) {
            throw new IllegalArgumentException("Couldn't parse [" + command + "]");
        }
    }

}
