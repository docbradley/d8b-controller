package com.adamdbradley.mcu.demo.smoketest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import com.adamdbradley.mcu.MCUMidiPort;
import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelLED;
import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.PanelLED;
import com.adamdbradley.mcu.console.SignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.UniversalDeviceQuery;
import com.adamdbradley.mcu.console.protocol.command.LightChannelLED;
import com.adamdbradley.mcu.console.protocol.command.LightPanelLED;
import com.adamdbradley.mcu.console.protocol.command.MoveFader;
import com.adamdbradley.mcu.console.protocol.command.RequestSerialNumber;
import com.adamdbradley.mcu.console.protocol.command.SetGlobalSignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.command.SetSignalLevel;
import com.adamdbradley.mcu.console.protocol.command.SetSignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.command.ShutChannelLED;
import com.adamdbradley.mcu.console.protocol.command.ShutPanelLED;
import com.adamdbradley.mcu.console.protocol.command.WriteScreen;
import com.adamdbradley.mcu.console.protocol.command.WriteTimecode;
import com.adamdbradley.mcu.console.protocol.command.WriteVPot;
import com.google.common.base.Strings;

public class MasterSmokeTest
implements Runnable, AutoCloseable {

    private final MCUMidiPort port;

    public MasterSmokeTest(List<Info> candidates) throws MidiUnavailableException {
        port = openPort(candidates);
    }

    private MCUMidiPort openPort(List<Info> candidates) throws MidiUnavailableException {
        if (MidiSystem.getMidiDevice(candidates.get(0)).getMaxReceivers() != 0) {
            return new MCUMidiPort(candidates.get(1), candidates.get(0));
        } else {
            return new MCUMidiPort(candidates.get(0), candidates.get(1));
        }
    }

    @Override
    public void close() {
        port.close();
    }

    public static final String MESSAGE = "d8b-controller-MCU 0.0.0";

    @Override
    public void run() {
        try {
            port.send(new UniversalDeviceQuery());
            port.send(new RequestSerialNumber(DeviceType.Master));

            Thread.sleep(1000);

            port.send(new SetGlobalSignalLevelDisplayMode(DeviceType.Master,
                    SignalLevelDisplayMode.OFF));
            port.send(new WriteScreen(DeviceType.Master,
                    0, 0, 
                    Strings.repeat("        ", 14)
                    ));
            port.send(new WriteScreen(DeviceType.Master,
                    0, 0x38 - 1 - MESSAGE.length(), MESSAGE));

            for (byte pos=0; pos<=0x0B; pos++) {
                for (byte ch=0; ch<0x40; ch++) {
                    for (boolean dot: new boolean[] { false, true }) {
                        port.send(new WriteTimecode(pos, ch, dot));
                        Thread.sleep(10);
                    }
                }
                port.send(new WriteTimecode(pos, (byte) 0x0, false));
            }

            for (PanelLED led: PanelLED.values()) {
                port.send(new LightPanelLED(led));
                Thread.sleep(100);
            }
            for (PanelLED led: PanelLED.values()) {
                port.send(new ShutPanelLED(led));
                Thread.sleep(100);
            }

            for (SignalLevelDisplayMode mode: SignalLevelDisplayMode.values()) {
                if (mode == SignalLevelDisplayMode.OFF) {
                    continue;
                }
                for (final Channel channel: Channel.values()) {
                    port.send(new SetSignalLevelDisplayMode(DeviceType.Master, channel, mode));
                }
                for (int i=0; i<=15; i++) {
                    for (final Channel channel: Channel.values()) {
                        port.send(new SetSignalLevel(channel, i));
                    }
                    Thread.sleep(125);
                }
            }

            port.send(new SetGlobalSignalLevelDisplayMode(DeviceType.Master,
                    SignalLevelDisplayMode.INDICATOR));

            for (final Channel channel: Channel.values()) {
                for (final ChannelLED led: ChannelLED.values()) {
                    port.send(new LightChannelLED(channel, led));
                    Thread.sleep(100);
                }
            }

            for (final Channel channel: Channel.values()) {
                for (final ChannelLED led: ChannelLED.values()) {
                    port.send(new ShutChannelLED(channel, led));
                    Thread.sleep(100);
                }
            }

            for (final Fader fader: Fader.values()) {
                port.send(new MoveFader(fader, 0));
                Thread.sleep(200);
                port.send(new MoveFader(fader, 1023));
                Thread.sleep(200);
                port.send(new MoveFader(fader, 0));
            }

            for (final WriteVPot.VPotMode mode: WriteVPot.VPotMode.values()) {
                port.send(new WriteScreen(DeviceType.Master,
                        0, 0,
                        (mode.name() + "                    ").substring(0, 20)));
                for (final boolean dot: new boolean[] { false, true }) {
                    final int min;
                    final int max;
                    switch (mode) {
                    case SinglePosition:
                    case DirectionFanFromCenter:
                        min = -5;
                        max = 5;
                        break;
                    case FanFromLeft:
                        min = 1;
                        max = 11;
                        break;
                    case SpreadFromCenter:
                        min = 1;
                        max = 6;
                        break;
                    default:
                        throw new IllegalStateException();
                    }

                    // Dot only
                    for (final Channel channel: Channel.values()) {
                        port.send(new WriteVPot(channel, null, 0, dot));
                    }
                    Thread.sleep(25);

                    // All valid positions
                    for (int i=min; i<=max; i++) {
                        for (final Channel channel: Channel.values()) {
                            port.send(new WriteVPot(channel, mode, i, dot));
                        }
                        Thread.sleep(25);
                    }

                    // Clear
                    for (final Channel channel: Channel.values()) {
                        port.send(new WriteVPot(channel, null, 0, false));
                    }
                }

                port.send(new WriteScreen(DeviceType.Master,
                        0, 0, "                    "));
            }

            Thread.sleep(1800000);
        } catch (InvalidMidiDataException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }





    public static void main(final String[] argv) throws Exception {
        final List<MidiDevice.Info> infos = Arrays.asList(MidiSystem.getMidiDeviceInfo());
        final List<MidiDevice.Info> candidates = new ArrayList<>(2);
        for (final MidiDevice.Info info: infos) {
            if (info.getName().startsWith("MCU Pro USB ")) {
                candidates.add(info);
            }
        }
        if (candidates.size() != 2) {
            throw new IllegalStateException("Didn't find what I'm looking for: " + infos);
        }

        try (MasterSmokeTest snooper = new MasterSmokeTest(candidates)) {
            snooper.run();
        }
    }

}
