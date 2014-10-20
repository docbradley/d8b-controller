package com.adamdbradley.mcu.console.protocol.command;

import org.junit.Test;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelLED;
import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.PanelLED;
import com.adamdbradley.mcu.console.SignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.command.WriteVPot.VPotMode;
import com.google.common.base.Strings;

import static org.junit.Assert.*;

/**
 * Assert that all MCU Commands have symmetrical codecs.
 */
public class TestCommands {

    final CommandParser parser = new CommandParser();

    @Test
    public void lightAndShutChannelLED() throws Exception {
        for (Channel channel: Channel.values()) {
            for (ChannelLED led: ChannelLED.values()) {
                final LightChannelLED command = new LightChannelLED(channel, led);
                assertEquals(command,
                        parser.parse(command.getMessage()));

                final ShutChannelLED command2 = new ShutChannelLED(channel, led);
                assertEquals(command2,
                        parser.parse(command2.getMessage()));
            }
        }
    }

    @Test
    public void lightAndShutPanelLED() throws Exception {
        for (PanelLED led: PanelLED.values()) {
            if (led == PanelLED.Timecode_BEATS || led == PanelLED.Timecode_SMPTE) {
                continue;
            }

            final LightPanelLED command = new LightPanelLED(led);
            assertEquals(command,
                    parser.parse(command.getMessage()));

            final ShutPanelLED command2 = new ShutPanelLED(led);
            assertEquals(command2,
                    parser.parse(command2.getMessage()));
        }
    }

    @Test
    public void moveFader() throws Exception {
        for (Fader fader: Fader.values()) {
            for (int value=0; value<1024; value++) {
                final MoveFader command = new MoveFader(fader, value);
                assertEquals(command,
                        parser.parse(command.getMessage()));
            }
        }
    }

    @Test
    public void reboot1() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            assertEquals(new Reboot1(deviceType),
                    parser.parse(new Reboot1(deviceType).getMessage()));
        }
    }

    @Test
    public void reboot2() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            assertEquals(new Reboot2(deviceType),
                    parser.parse(new Reboot2(deviceType).getMessage()));
        }
    }

    @Test
    public void requestSerialNumber() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            assertEquals(new RequestSerialNumber(deviceType),
                    parser.parse(new RequestSerialNumber(deviceType).getMessage()));
        }
    }

    @Test
    public void requestVersion() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            assertEquals(new RequestVersion(deviceType),
                    parser.parse(new RequestVersion(deviceType).getMessage()));
        }
    }

    @Test
    public void setGlobalSignalLevelDisplayMode() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            for (final SignalLevelDisplayMode mode: SignalLevelDisplayMode.values()) {
                assertEquals(new SetGlobalSignalLevelDisplayMode(deviceType, mode),
                        parser.parse(new SetGlobalSignalLevelDisplayMode(deviceType, mode).getMessage()));
            }
        }
    }

    @Test
    public void setSignalLevel() throws Exception {
        for (final Channel channel: Channel.values()) {
            for (int value=0; value<0x10; value++) {
                assertEquals(new SetSignalLevel(channel, value),
                        parser.parse(new SetSignalLevel(channel, value).getMessage()));
            }
        }
    }

    @Test
    public void setSignalLevelDisplayMode() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            for (final SignalLevelDisplayMode mode: SignalLevelDisplayMode.values()) {
                for (final Channel channel: Channel.values()) {
                    assertEquals(new SetSignalLevelDisplayMode(deviceType, channel, mode),
                            parser.parse(new SetSignalLevelDisplayMode(deviceType, channel, mode).getMessage()));
                }
            }
        }
    }

    @Test
    public void wakeUp() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            assertEquals(new WakeUp(deviceType),
                    parser.parse(new WakeUp(deviceType).getMessage()));
        }
    }

    @Test
    public void writeScreen() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            for (int row=0; row<2; row++) {
                for (int column=0; column<0x38; column++) {
                    for (char character: WriteScreen.encodableCharacters()) {
                        for (int i: new int[] { 1, 2, 10, 20 }) {
                            final WriteScreen command = new WriteScreen(deviceType, row, column,
                                    Strings.repeat("" + character, i));
                            assertEquals(command,
                                    parser.parse(command.getMessage()));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void writeTimecode() throws Exception {
        for (int i=0; i<10; i++) {
            assertTrue(WriteTimecode.supportedChars()
                    .contains(Integer.toString(i).charAt(0)));
        }
        for (final boolean dot: new boolean[] { false, true}) {
            for (byte position=0; position<=0x0B; position++) {
                for (char character: WriteTimecode.supportedChars()) {
                    assertEquals(new WriteTimecode(position, character, dot),
                            parser.parse(new WriteTimecode(position, character, dot).getMessage()));
                }
            }
        }
    }

    @Test
    public void writeVPot() throws Exception {
        for (final Channel channel: Channel.values()) {
            for (final VPotMode mode: VPotMode.values()) {
                for (final boolean dot: new boolean[] { false, true }) {
                    for (int value=mode.min(); value<=mode.max(); value++) {
                        assertEquals(new WriteVPot(channel, mode, value, dot),
                                parser.parse(new WriteVPot(channel, mode, value, dot).getMessage()));
                    }
                }
            }
        }
    }

}
