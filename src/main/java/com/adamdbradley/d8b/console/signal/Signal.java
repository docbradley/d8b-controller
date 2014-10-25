package com.adamdbradley.d8b.console.signal;

import java.time.Instant;

import com.adamdbradley.d8b.AppClock;

/**
 * Instances represent surface signals sent from the d8b console.
 */
public abstract class Signal {

    public final Instant timestamp;
    public final SignalType type;

    protected Signal(final SignalType type) {
        this.type = type;
        this.timestamp = AppClock.now();
    }

    @Override
    public abstract String toString();

}
