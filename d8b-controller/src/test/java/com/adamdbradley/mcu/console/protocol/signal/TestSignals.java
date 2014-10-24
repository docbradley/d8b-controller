package com.adamdbradley.mcu.console.protocol.signal;

import java.util.Random;

import org.junit.Test;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelButton;
import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.PanelButton;

import static org.junit.Assert.*;

/**
 * Assert that all MCU Signals have symmetrical codecs.
 */
public class TestSignals {

    private static final Random rng = new Random();

    public final SignalParser parser = new SignalParser();

    @Test
    public void buttonPressedAndReleased() throws Exception {
        for (final PanelButton button: PanelButton.values()) {
            assertEquals(new ButtonPressed(button),
                    parser.parse(new ButtonPressed(button).getMessage()));
            assertEquals(new ButtonReleased(button),
                    parser.parse(new ButtonReleased(button).getMessage()));
        }
    }

    @Test
    public void channelButtonPressedAndReleased() throws Exception {
        for (final ChannelButton button: ChannelButton.values()) {
            for (final Channel channel: Channel.values()) {
                assertEquals(new ChannelButtonPressed(channel, button),
                        parser.parse(new ChannelButtonPressed(channel, button).getMessage()));
                assertEquals(new ChannelButtonReleased(channel, button),
                        parser.parse(new ChannelButtonReleased(channel, button).getMessage()));
            }
        }
    }

    @Test
    public void channelVPotMoved() throws Exception {
        for (final Channel channel: Channel.values()) {
            for (int velocity=-15; velocity<=15; velocity++) {
                if (velocity == 0) {
                    continue;
                }
                assertEquals(new ChannelVPotMoved(channel, velocity),
                        parser.parse(new ChannelVPotMoved(channel, velocity).getMessage()));
            }
        }
    }

    @Test
    public void faderMoved() throws Exception {
        for (final Fader fader: Fader.values()) {
            for (int value=0; value<1024; value++) {
                assertEquals(new FaderMoved(fader, value),
                        parser.parse(new FaderMoved(fader, value).getMessage()));
            }
        }
    }

    @Test
    public void faderTouchedAndReleased() throws Exception {
        for (final Fader fader: Fader.values()) {
            assertEquals(new FaderTouched(fader),
                    parser.parse(new FaderTouched(fader).getMessage()));
            assertEquals(new FaderReleased(fader),
                    parser.parse(new FaderReleased(fader).getMessage()));
        }
    }

    @Test
    public void jogLeft() throws Exception {
        assertEquals(new JogLeft(),
                parser.parse(new JogLeft().getMessage()));
    }

    @Test
    public void jogRight() throws Exception {
        assertEquals(new JogRight(),
                parser.parse(new JogRight().getMessage()));
    }

    @Test
    public void reportSerialNumber() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            for (int i=0; i<1000; i++) {
                final byte[] serialNumber = new byte[] {
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128)
                };
                assertEquals(new ReportSerialNumber(deviceType, serialNumber),
                        parser.parse(new ReportSerialNumber(deviceType, serialNumber).getMessage()));
            }
        }
    }

    @Test
    public void reportVersion() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            for (int i=0; i<1000; i++) {
                final byte[] version = new byte[] {
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128)
                };
                assertEquals(new ReportVersion(deviceType, version),
                        parser.parse(new ReportVersion(deviceType, version).getMessage()));
            }
        }
    }

    @Test
    public void reportAwake() throws Exception {
        for (final DeviceType deviceType: DeviceType.values()) {
            for (int i=0; i<1000; i++) {
                final byte[] initId = new byte[] {
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128),
                        (byte) rng.nextInt(128)
                };
                assertEquals(new ReportAwake(deviceType, initId),
                        parser.parse(new ReportAwake(deviceType, initId).getMessage()));
            }
        }
    }

}
