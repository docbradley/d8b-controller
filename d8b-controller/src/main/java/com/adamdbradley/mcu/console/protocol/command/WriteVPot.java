package com.adamdbradley.mcu.console.protocol.command;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.protocol.ChannelControlChangeConsoleMessage;
import com.adamdbradley.mcu.console.protocol.Command;

public class WriteVPot
extends ChannelControlChangeConsoleMessage
implements Command {

    public enum VPotMode {
        /**
         * Value must be 1 (left) throught 6 (center) through 11 (right).
         */
        SinglePosition,

        /**
         * Value must be 1 (left) throught 6 (center) through 11 (right).
         */
        DirectionFanFromCenter,

        /**
         * Value must be 1 (left) through 11 (full).
         */
        FanFromLeft,

        /**
         * Value must be 1 (center only) through 6 (full).
         */
        SpreadFromCenter
    }

    /**
     * 
     * @param channel
     * @param mode <code>null</code> to turn off; non-<code>null</code> must specify a displayed value.
     * @param value See {@link VPotMode} for value constraints
     * @param dot
     * @throws InvalidMidiDataException
     */
    public WriteVPot(final Channel channel, final VPotMode mode, final int value, final boolean dot) {
        super((byte) 0x0, (byte) (0x30 | channel.ordinal()),
                mapCommand(mode, value, dot));
    }

    private static byte mapCommand(final VPotMode mode, final int value, final boolean dot) {
        return (byte) (mapCommand(mode, value) | (dot ? 0x40 : 0));
    }

    private static byte mapCommand(final VPotMode mode, final int value) {
        if (mode == null) {
            return 0;
        } else switch (mode) {
        case SinglePosition:
            if (value < -5 || value > 5) {
                throw new IllegalArgumentException(mode + " value must be in [-5, 5]");
            }
            return (byte) (0x06 + value);

        case DirectionFanFromCenter:
            if (value < -5 || value > 5) {
                throw new IllegalArgumentException(mode + " value must be in [-5, 5]");
            }
            return (byte) (0x16 + value);

        case FanFromLeft:
            if (value < 1 || value > 11) {
                throw new IllegalArgumentException(mode + " value must be in [1, 11]");
            }
            return (byte) (0x20 + value);

        case SpreadFromCenter:
            if (value < 1 || value > 6) {
                throw new IllegalArgumentException(mode + " value must be in [1, 6]");
            }
            return (byte) (0x30 + value);

        default:
            throw new IllegalArgumentException("Unknown " + mode);
        }
    }

}
