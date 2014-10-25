package com.adamdbradley.mcu.actor;

import java.util.concurrent.Callable;

import com.adamdbradley.mcu.MCUClientPort;

public abstract class MCUActor
implements Callable<Void> {

    protected final MCUClientPort port;

    protected MCUActor(final MCUClientPort port) {
        this.port = port;
    }

}
