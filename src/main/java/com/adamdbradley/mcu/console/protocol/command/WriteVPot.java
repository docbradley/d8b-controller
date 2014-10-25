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
         * Value must be -5 (left) through 0 (center) through 5 (right).
         * Wire value is 1 (left) through 6 (center) through 11 (right).
         */
        SinglePosition(-5, 5),

        /**
         * Value must be -5 (left) through 0 (center) through 5 (right).
         * Wire value is 1 (left) throught 6 (center) through 11 (right).
         */
        DirectionFanFromCenter (-5, 5),

        /**
         * Value must be 1 (left) through 11 (full).
         * Wire value is the same.
         */
        FanFromLeft(1, 11),

        /**
         * Value must be 1 (center only) through 6 (full).
         * Wire value is the same.
         */
        SpreadFromCenter(1, 6);

        private final int min;
        private final int max;

        VPotMode(final int min, final int max) {
            this.min = min;
            this.max = max;
        }

        public int min() { return min; }
        public int max() { return max; }

    }

    public final Channel channel;
    public final VPotMode mode;
    public final int value;
    public final boolean dot;

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

        this.channel = channel;
        this.mode = mode;
        this.value = value;
        this.dot = dot;
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
