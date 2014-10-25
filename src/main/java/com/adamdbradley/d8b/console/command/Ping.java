package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.signal.Pong;

/**
 * Ask that a {@link Pong} be sent back.
 */
public class Ping extends UnparameterizedCommand {

    public Ping() {
        super(CommandType.Ping);
    }

}
