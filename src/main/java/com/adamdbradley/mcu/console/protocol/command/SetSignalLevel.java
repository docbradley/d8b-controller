package com.adamdbradley.mcu.console.protocol.command;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.protocol.ChannelPressureConsoleMessage;
import com.adamdbradley.mcu.console.protocol.Command;

public class SetSignalLevel
extends ChannelPressureConsoleMessage
implements Command {

    public final Channel channel;
    public final int value;

    /**
     * @param channel
     * @param value 0 to 15
     * @throws InvalidMidiDataException
     */
    public SetSignalLevel(final Channel channel, final int value)
            throws InvalidMidiDataException {
        super((byte) 0x0, encode(channel, value));

        this.channel = channel;
        this.value = value;
    }

    private static byte encode(final Channel channel, final int value) {
        if (value < 0 || value > 0x0F) {
            throw new IllegalArgumentException("Value must be in [0, 15]: " + value);
        }
        return (byte) ((channel.ordinal() << 4) | value);
    }

}
