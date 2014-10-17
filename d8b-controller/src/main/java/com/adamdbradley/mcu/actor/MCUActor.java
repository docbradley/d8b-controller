package com.adamdbradley.mcu.actor;

import java.util.concurrent.Callable;

import com.adamdbradley.mcu.MCUMidiPort;

public abstract class MCUActor
implements Callable<Void> {

    protected final MCUMidiPort port;

    protected MCUActor(final MCUMidiPort port) {
        this.port = port;
    }

}
