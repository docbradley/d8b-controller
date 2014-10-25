package com.adamdbradley.d8b.actor;

import java.util.concurrent.Callable;

import com.adamdbradley.d8b.console.Fader;
import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.d8b.console.command.LightAllLEDs;
import com.adamdbradley.d8b.console.command.MoveFader;
import com.adamdbradley.d8b.console.command.ScreenHelper;
import com.adamdbradley.d8b.console.command.SetChannelNumber;
import com.adamdbradley.d8b.console.command.ShutAllLEDs;
import com.adamdbradley.d8b.console.command.TimecodeHelper;

public class Reset
extends Actor
implements Callable<Void> {

    public Reset(final ConsoleControlConnection console) {
        super(console, null);
    }

    @Override
    public Void call() throws InterruptedException {

        console.send(new LightAllLEDs());
        Thread.sleep(250);
        console.send(new ShutAllLEDs());

        console.send(ScreenHelper.buildDrawScreenCommands(0, 0, "Controller Platform d8b-controller 0.0.0"));
        console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "(c)2014 Adam D. Bradley, All Rights Rsvd"));

        console.send(new SetChannelNumber("FF"));

        console.send(TimecodeHelper.buildRedrawTimecodeCommands("            "));

        for (final Fader fader: Fader.values()) {
            console.send(new MoveFader(fader, 0));
        }

        return null;
    }

}
