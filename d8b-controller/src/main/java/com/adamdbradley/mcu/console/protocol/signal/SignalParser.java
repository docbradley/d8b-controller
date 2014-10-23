package com.adamdbradley.mcu.console.protocol.signal;

import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelButton;
import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.PanelButton;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Signal;

public class SignalParser {

    // VPot button Note On        ch0 0x20-0x27 0x00/0x7F
    // REC:        Note On        ch0 0x00-0x07 0x00/0x7F
    // SOLO:       Note On        ch0 0x08-0x0F 0x00/0x7F
    // MUTE:       Note On        ch0 0x10-0x18 0x00/0x7F
    // SELECT:     Note On        ch0 0x18-0x1F 0x00/0x7F
    // Buttons:    Note On        ch0 0x28-0x65 0x00/0x7F
    // F TOUCH:    Note On        ch0 0x68-0x6F 0x00/0x7F
    // ???         Control Change ch0 0x2E      0x01-0x7F (TODO: Footswitch maybe?)
    // JOG:        Control Change ch0 0x3C      0x01/0x41 (0x01 = right, 0x41 = left)
    // VPot left:  Control change ch0 0x10-0x17 0x00-0x7F (0x00-3F=right, 0x40-0x4F=left)
    // F MOVE:     Pitch Bend     chN LSB(3msb) MSB(full) // = 10 bits of functional resolution

    public Signal parse(final MidiMessage message) throws InvalidMidiDataException {
        final byte messageChannel = (byte) (message.getStatus() & 0x0F);
        switch (message.getStatus() & (byte) 0xF0) {
        case 0x80:
            // Note off -- not used for anything
            return null;

        case 0x90:
            // Note on -- used for LOTS of stuff
            if (messageChannel != 0x00) {
                return null;
            }
            // Note on
            final byte noteOnIdentifier = (byte) (message.getMessage()[1] & 0xF8);
            final int channelButtonChannelNumber = message.getMessage()[1] & 0x07;
            final Channel touchedChannel = Channel.values()[channelButtonChannelNumber];

            switch (noteOnIdentifier) {
            case 0x00: // REC
            case 0x08: // SOLO
            case 0x10: // MUTE
            case 0x18: // SELECT
            case 0x20: // VPot
                final ChannelButton button = ChannelButtonPressed.decode(noteOnIdentifier);
                if (message.getMessage()[2] != 0x00) {
                    return new ChannelButtonPressed(touchedChannel, button);
                } else {
                    return new ChannelButtonReleased(touchedChannel, button);
                }
            case 0x68: // Fader Touch
                if (message.getMessage()[2] != 0x00) {
                    return new FaderTouched(touchedChannel.fader());
                } else {
                    return new FaderReleased(touchedChannel.fader());
                }
            case 0x70: // Master fader touch
                if (message.getMessage()[2] != 0x00) {
                    return new FaderTouched(Fader.Master);
                } else {
                    return new FaderReleased(Fader.Master);
                }
            default: // Panel
                final PanelButton panelButton = ButtonMessage.decode(message.getMessage()[1]);
                if (panelButton != null) {
                    if (message.getMessage()[2] != 0x00) {
                        return new ButtonPressed(panelButton);
                    } else {
                        return new ButtonReleased(panelButton);
                    }
                } else {
                    // Unknown ID, report nothing
                    return null;
                }
            }
        case 0xA0:
            // Polyphonic Key Pressure -- UNUSED
            return null;

        case 0xB0:
            // Control Change
            if (messageChannel != 0x00) {
                return null;
            }
            switch (message.getMessage()[1]) {
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
            case 0x16:
            case 0x17:
                // VPot moved
                return new ChannelVPotMoved(
                        Channel.values()[message.getMessage()[1] & 0x0F],
                        ((message.getMessage()[2] & 0x40) == 0)
                            ? message.getMessage()[2]
                            : -(message.getMessage()[2] & 0x0F)
                        );
            case 0x3C:
                if ((message.getMessage()[2] & 0x40) != 0) {
                    return new JogLeft();
                } else {
                    return new JogRight();
                }
            default:
                return null;
            }

        case 0xC0:
            // Program Change -- UNUSED
            return null;

        case 0xD0:
            // Channel Pressure -- UNUSED
            return null;

        case 0xE0:
            // Pitch Bend: FADER
            final Fader fader = Fader.values()[messageChannel];
            final int value = (message.getMessage()[2] << 3) | (message.getMessage()[1] >> 4);
            return new FaderMoved(fader, value);

        case 0xF0:
            // SysEx
            if (message.getLength() > 7) {
                final byte[] payload = message.getMessage();
                if (payload[0] == (byte) 0xF0
                        && payload[1] == (byte) 0x00
                        && payload[2] == (byte) 0x00
                        && payload[3] == (byte) 0x66) {
                    final DeviceType device;
                    try {
                        device = DeviceType.decode(payload[4]);
                    } catch (IllegalArgumentException e) {
                        // Unrecognized Mackie product code
                        return null;
                    }

                    switch (payload[5]) {
                    case 0x00:
                        // Wake-up -- Command, not signal
                        return null;
                    case 0x01:
                        // Wake-up message
                        return new ReportAwake(device, Arrays.copyOfRange(payload, 6, 17));
                    case 0x08:
                        // Reboot1 -- Command, not signal
                    case 0x09:
                        // Reboot2 -- Command, not signal
                    case 0x12:
                        // Screen write -- Command, not signal
                    case 0x13:
                        // Request version number -- Command, not signal
                        return null;
                    case 0x14:
                        // Version number report: 5 bytes
                        return new ReportVersion(device, Arrays.copyOfRange(payload, 6, 11));
                    case 0x1A:
                        // Serial number request -- Command, not signal
                        return null;
                    case 0x1B:
                        // Serial number report: 6 bytes
                        return new ReportSerialNumber(device, Arrays.copyOfRange(payload, 6, 12));
                    default:
                        // Unknown message type
                        return null;
                    }
                } else {
                    // Not a Mackie SysEx
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
