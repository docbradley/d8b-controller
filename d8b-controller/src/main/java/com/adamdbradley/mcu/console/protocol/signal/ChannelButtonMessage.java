package com.adamdbradley.mcu.console.protocol.signal;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelButton;
import com.adamdbradley.mcu.console.protocol.NoteOnMessageBase;

public class ChannelButtonMessage
extends NoteOnMessageBase {

    public final Channel channel;
    public final ChannelButton channelButton;

    public ChannelButtonMessage(final Channel channel,
            final ChannelButton channelButton, final boolean pressed) {
        super((byte) 0x0,
                encode(channel, channelButton), 
                (byte) (pressed ? 0x7F : 0x00));
        this.channel = channel;
        this.channelButton = channelButton;
    }

    // XXX: pull up to common superclass
    static byte encode(final Channel channel, final ChannelButton button) {
        switch (button) {
        case REC:
            return (byte) (0x00 + channel.ordinal());
        case SOLO:
            return (byte) (0x08 + channel.ordinal());
        case MUTE:
            return (byte) (0x10 + channel.ordinal());
        case SELECT:
            return (byte) (0x18 + channel.ordinal());
        case VPot:
            return (byte) (0x20 + channel.ordinal());
        default:
            throw new IllegalArgumentException(channel + ":" + button);
        }
    }

    public static ChannelButton decode(final byte b) {
        switch (b) {
        case 0x00: // REC
            return ChannelButton.REC;
        case 0x08: // SOLO
            return ChannelButton.SOLO;
        case 0x10: // MUTE
            return ChannelButton.MUTE;
        case 0x18: // SELECT
            return ChannelButton.SELECT;
        case 0x20: // VPot
            return ChannelButton.VPot;
        default:
            throw new IllegalArgumentException("Unknown " + Integer.toHexString(b));
        }
    }

}
