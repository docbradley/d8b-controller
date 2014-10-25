package com.adamdbradley.mcu.console.protocol.command;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelLED;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.NoteOnConsoleMessage;

abstract class ChannelLEDCommandBase
extends NoteOnConsoleMessage
implements Command {

    public final Channel channel;
    public final ChannelLED led;

    public ChannelLEDCommandBase(final Channel channel, final ChannelLED led, final boolean light) {
        super((byte) 0,
                encode(led, channel),
                (byte) (light ? 0x7F : 0x00));
        this.channel = channel;
        this.led = led;
    }

    protected static byte encode(final ChannelLED led, final Channel channel) {
        switch (led) {
        case REC:
            return (byte) (0x00 + channel.ordinal());
        case SOLO:
            return (byte) (0x08 + channel.ordinal());
        case MUTE:
            return (byte) (0x10 + channel.ordinal());
        case SELECT:
            return (byte) (0x18 + channel.ordinal());
        default:
            throw new IllegalStateException("Unknown " + led);
        }
    }

}
