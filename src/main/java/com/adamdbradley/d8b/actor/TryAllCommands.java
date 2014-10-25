package com.adamdbradley.d8b.actor;

import java.util.concurrent.Callable;

import com.adamdbradley.d8b.console.Fader;
import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.d8b.console.MeterLEDChannel;
import com.adamdbradley.d8b.console.MeterLEDNumber;
import com.adamdbradley.d8b.console.PanelLED;
import com.adamdbradley.d8b.console.TimecodePosition;
import com.adamdbradley.d8b.console.VPot;
import com.adamdbradley.d8b.console.VPotLED;
import com.adamdbradley.d8b.console.VPotLEDPosition;
import com.adamdbradley.d8b.console.command.BlinkPanelLED;
import com.adamdbradley.d8b.console.command.BlinkVPotLED;
import com.adamdbradley.d8b.console.command.LightAllLEDs;
import com.adamdbradley.d8b.console.command.LightMeterLED;
import com.adamdbradley.d8b.console.command.LightPanelLED;
import com.adamdbradley.d8b.console.command.LightVPotLED;
import com.adamdbradley.d8b.console.command.MoveFader;
import com.adamdbradley.d8b.console.command.ScreenHelper;
import com.adamdbradley.d8b.console.command.SetChannelNumber;
import com.adamdbradley.d8b.console.command.ShutAllLEDs;
import com.adamdbradley.d8b.console.command.ShutMeterLED;
import com.adamdbradley.d8b.console.command.ShutPanelLED;
import com.adamdbradley.d8b.console.command.ShutVPotLED;
import com.adamdbradley.d8b.console.command.TimecodeHelper;
import com.adamdbradley.d8b.console.command.UpdateTimecode;

public class TryAllCommands
extends Actor
implements Callable<Void> {

    public TryAllCommands(final ConsoleControlConnection console) {
        super(console, null);
    }

    @Override
    public Void call() throws InterruptedException {

        // Blank the screen... 2 X 40
        console.send(ScreenHelper.buildDrawScreenCommands(0, 0, "HERE WE GO...                           "));
        console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "                                        "));

        console.send(new LightAllLEDs());
        Thread.sleep(250);
        console.send(new ShutAllLEDs());
        Thread.sleep(250);
        console.send(new LightAllLEDs());
        Thread.sleep(250);
        console.send(new ShutAllLEDs());

        // Blank the screen... 2 X 40
        console.send(ScreenHelper.buildDrawScreenCommands(0, 0, "                                        "));
        // console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "                                        "));

        final String message1 = "Hello World!";
        for (int i=0; i<=(40 - message1.length()); i++) {
            if (i == 0) {
                console.send(ScreenHelper.buildDrawScreenCommands(0, 0, message1));
            } else {
                console.send(ScreenHelper.buildDrawScreenCommands(0, i-1, " " + message1));
            }
            console.send(ScreenHelper.buildDrawScreenCommands(1, 40-message1.length()-i, message1 + " "));
            Thread.sleep(100);
        }

        console.send(ScreenHelper.buildDrawScreenCommands(0, 0, "Controller Platform d8b-controller 0.0.0"));
        console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "(c)2014 Adam D. Bradley, All Rights Rsvd"));

        for (MeterLEDChannel channel: MeterLEDChannel.values()) {
            for (MeterLEDNumber number: MeterLEDNumber.values()) {
                console.send(new LightMeterLED(channel, number));
                Thread.sleep(3);
            }
        }
        for (MeterLEDChannel channel: MeterLEDChannel.values()) {
            for (MeterLEDNumber number: MeterLEDNumber.values()) {
                console.send(new ShutMeterLED(channel, number));
                Thread.sleep(3);
            }
        }

        for (int i=0; i<=99; i++) {
            console.send(new SetChannelNumber(Integer.toString(i)));
            Thread.sleep(10);
        }
        for (int i=98; i>=0; i--) {
            console.send(new SetChannelNumber(i));
            Thread.sleep(10);
        }
        for (int i=0; i<10; i++) {
            if (i % 2 == 0) {
                console.send(new SetChannelNumber("8F"));
            } else {
                console.send(new SetChannelNumber("F8"));
            }
            Thread.sleep(40);
        }
        console.send(new SetChannelNumber("FF"));

        for (int i=0; i<=9; i++) {
            for (final TimecodePosition position: TimecodePosition.values()) {
                console.send(new UpdateTimecode(position, i, false));
                Thread.sleep(10);
            }
        }
        for (int i=8; i>=0; i--) {
            for (final TimecodePosition position: TimecodePosition.values()) {
                console.send(new UpdateTimecode(position, i, true));
                Thread.sleep(10);
            }
        }
        console.send(TimecodeHelper.buildRedrawTimecodeCommands("            "));
        Thread.sleep(500);
        console.send(TimecodeHelper.buildRedrawTimecodeCommands(" . . . . . . . . . . . ."));
        Thread.sleep(500);
        console.send(TimecodeHelper.buildRedrawTimecodeCommands("------------"));
        Thread.sleep(500);
        console.send(TimecodeHelper.buildRedrawTimecodeCommands("            "));

        for (PanelLED led: PanelLED.values()) {
            console.send(new LightPanelLED(led));
            Thread.sleep(6);
        }
        for (PanelLED led: PanelLED.values()) {
            console.send(new BlinkPanelLED(led));
            Thread.sleep(6);
        }
        for (PanelLED led: PanelLED.values()) {
            console.send(new ShutPanelLED(led));
            Thread.sleep(6);
        }


        for (VPot vpot: VPot.values()) {
            for (VPotLEDPosition position: VPotLEDPosition.values()) {
                console.send(new LightVPotLED(new VPotLED(vpot, position)));
                Thread.sleep(5);
            }
        }
        for (VPot vpot: VPot.values()) {
            for (VPotLEDPosition position: VPotLEDPosition.values()) {
                console.send(new BlinkVPotLED(new VPotLED(vpot, position)));
                Thread.sleep(5);
            }
        }
        for (VPot vpot: VPot.values()) {
            for (VPotLEDPosition position: VPotLEDPosition.values()) {
                console.send(new ShutVPotLED(new VPotLED(vpot, position)));
                Thread.sleep(5);
            }
        }

        for (final Fader fader: Fader.values()) {
            console.send(new MoveFader(fader, 255));
            Thread.sleep(50);
        }
        for (final Fader fader: Fader.values()) {
            console.send(new MoveFader(fader, 0));
            Thread.sleep(50);
        }

        return null;
    }

}
