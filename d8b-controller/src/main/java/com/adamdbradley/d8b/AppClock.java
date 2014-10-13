package com.adamdbradley.d8b;

import java.time.Clock;
import java.time.Instant;

public class AppClock {

    public static final Clock CLOCK = Clock.systemUTC();

    public static Instant now() {
        return CLOCK.instant();
    }

}
