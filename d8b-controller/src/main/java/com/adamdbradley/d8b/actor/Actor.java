package com.adamdbradley.d8b.actor;

import java.util.concurrent.Callable;

import com.adamdbradley.d8b.audio.AudioControlConnection;
import com.adamdbradley.d8b.console.ConsoleControlConnection;

public abstract class Actor
implements Callable<Void> {

    protected final ConsoleControlConnection console;
    protected final AudioControlConnection audio;

    protected Actor(final ConsoleControlConnection console,
            final AudioControlConnection audio) {
        this.console = console;
        this.audio = audio;
    }

}
