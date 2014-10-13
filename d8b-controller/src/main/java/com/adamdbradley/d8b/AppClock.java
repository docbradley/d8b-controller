package com.adamdbradley.d8b;

import java.time.Clock;
import java.time.Instant;

/**
 * Convenience methods for interacting with the clock.
 */
public abstract class AppClock {

    public static final Clock CLOCK = Clock.systemUTC();

    public static Instant now() {
        return CLOCK.instant();
    }

}
