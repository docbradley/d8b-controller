package com.adamdbradley.mcu.console.protocol.command;

import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelButton;
import com.adamdbradley.mcu.console.ChannelLED;
import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.PanelButton;
import com.adamdbradley.mcu.console.PanelLED;
import com.adamdbradley.mcu.console.SignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.command.WriteVPot.VPotMode;
import com.adamdbradley.mcu.console.protocol.signal.ButtonMessage;
import com.adamdbradley.mcu.console.protocol.signal.ChannelButtonPressed;

/**
 * Parses {@link Command}s from {@link MidiMessage}s.
 */
public class CommandParser {

    public Command parse(final MidiMessage message) throws InvalidMidiDataException {
        final byte messageChannel = (byte) (message.getStatus() & 0x0F);
        switch (message.getStatus() & (byte) 0xF0) {
        case 0x80:
            // Note off -- not used for anything
            return null;

        case 0x90:
            // Note on -- used for LOTS of stuff in MIDI Channel 0

            if (messageChannel != 0x00) {
                return null;
            }

            final byte commandRange = (byte) (message.getMessage()[1] & 0xF8);
            switch (commandRange) {
            // 0x00 - 0x07 :: channel REC
            // 0x08 - 0x0F :: channel SOLO
            // 0x10 - 0x17 :: channel MUTE
            // 0x18 - 0x1F :: channel SELECT
            case 0x00:
            case 0x08:
            case 0x10:
            case 0x18:
                final Channel identifiedChannel = Channel.values()[message.getMessage()[1] & 0x07];
                final ChannelButton channelButton = ChannelButtonPressed.decode(commandRange);
                final ChannelLED channelLed = ChannelLED.valueOf(channelButton.name());
                if (message.getMessage()[2] != 0x00) {
                    return new LightChannelLED(identifiedChannel, channelLed);
                } else {
                    return new ShutChannelLED(identifiedChannel, channelLed);
                }

            // 0x28 - 0x65 :: panel LEDs
            case 0x28:
            case 0x30:
            case 0x38:
            case 0x40:
            case 0x48:
            case 0x50:
            case 0x58:
            case 0x60:
                final PanelButton button = ButtonMessage.decode(message.getMessage()[1]);
                final PanelLED led = PanelLED.valueOf(button.name());
                if (message.getMessage()[2] != 0x00) {
                    return new LightPanelLED(led);
                } else {
                    return new ShutPanelLED(led);
                }
            default:
                return null;
            }


        case 0xA0:
            // Polyphonic Key Pressure -- UNUSED
            return null;

        case 0xB0:
            // Control Change
            if (messageChannel != 0x00) {
                return null;
            }

            // VPot
            // Controller 0x30 + ch,  0xN? - mode, 0x?N - value (0 = off, 0x01 - 0x0B == position)
            final byte controllerNumber = message.getMessage()[1];
            if ((controllerNumber & 0xC0) == 0x30) {
                final Channel vpotChannel = Channel.values()[controllerNumber & 0x07];
                final byte position = message.getMessage()[2];
                final VPotMode mode = VPotMode.values()[(position & 0x30) >> 4];
                final boolean dot = ((position & 0x40) != 0);
                final int rawValue = position & 0x0F;
                if (rawValue == 0) {
                    return new WriteVPot(vpotChannel, null, 0, dot);
                } else switch (mode) {
                case SinglePosition:
                case DirectionFanFromCenter:
                    return new WriteVPot(vpotChannel, mode,
                            Math.min(rawValue - 6, 5), dot);
                case FanFromLeft:
                    return new WriteVPot(vpotChannel, mode,
                            Math.min(rawValue, 11), dot);
                case SpreadFromCenter:
                    return new WriteVPot(vpotChannel, mode,
                            Math.min(rawValue, 6), dot);
                default:
                    return null;
                }
            } else if ((controllerNumber & 0xC0) == 0x40) {
                // Write timecode
                return new WriteTimecode((byte) (controllerNumber & 0x0F),
                        (byte) (message.getMessage()[2] & 0x3F), // TODO: Character LUT
                        (message.getMessage()[2] & 0x40) != 0);
            }

        case 0xC0:
            // Program Change -- UNUSED
            return null;

        case 0xD0:
            // Channel Pressure -- SIGNAL LEVEL
            if (messageChannel != 0) {
                return null;
            }
            final Channel signalChannel = Channel.values()[message.getMessage()[2] >> 4];
            return new SetSignalLevel(signalChannel, message.getMessage()[2] & 0x0F);

        case 0xE0:
            // Pitch Bend: FADER
            final Fader fader = Fader.values()[messageChannel];
            return new MoveFader(fader,
                    ((message.getMessage()[1] & 0x7F) | ((message.getMessage()[2] & 0x7F) << 7)) >> 4
                    );

        case 0xF0:
            // SysEx
            if (message.getLength() > 7) {
                final byte[] payload = message.getMessage();
                if (payload[0] == (byte) 0xF0
                        && payload[1] == (byte) 0x00
                        && payload[2] == (byte) 0x00
                        && payload[3] == (byte) 0x66) {
                    final DeviceType deviceType;
                    try {
                        deviceType = DeviceType.decode(payload[4]);
                    } catch (IllegalArgumentException e) {
                        // Unrecognized Mackie product code
                        return null;
                    }

                    switch (payload[5]) {
                    case 0x00:
                        // Wake-up -- Command, not signal
                        return new WakeUp(deviceType);
                    case 0x01:
                        // Wake-up signal -- Signal, not Command
                        return null;
                    case 0x08:
                        return new Reboot1(deviceType);
                    case 0x09:
                        return new Reboot2(deviceType);
                    case 0x12:
                        // Screen write
                        int position = payload[6];
                        return new WriteScreen(deviceType,
                                position / 0x38, 
                                position % 0x38,
                                Arrays.copyOfRange(payload, 7, payload.length - 1));
                    case 0x13:
                        return new RequestVersion(deviceType);
                    case 0x14:
                        // Version number report -- Signal, not Command
                        return null;
                    case 0x1A:
                        // Serial number request -- Command, not signal
                        return new RequestSerialNumber(deviceType);
                    case 0x1B:
                        // Serial number report -- Signal, not Command
                        return null;
                    case 0x20:
                        // Per-Channel Signal Level Display
                        return new SetSignalLevelDisplayMode(deviceType,
                                Channel.values()[payload[6]],
                                SignalLevelDisplayMode.values()[payload[7]]);
                    case 0x21:
                        // Global Signal Level Display
                        return new SetGlobalSignalLevelDisplayMode(deviceType,
                                SignalLevelDisplayMode.values()[payload[6]]);
                    default:
                        // Unknown message type
                        return null;
                    }
                } else {
                    // Not a Mackie-specific SysEx

                    

                    return null;
                }
            }
            System.err.println("SYSEX: " + MCUSysexMessage.toString(message));
            return null;

        default:
            // Bogus MIDI message -- log it?
            return null;
        }
    }

}
