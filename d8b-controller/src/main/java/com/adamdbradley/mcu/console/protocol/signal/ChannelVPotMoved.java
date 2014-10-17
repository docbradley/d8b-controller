package com.adamdbradley.mcu.console.protocol.signal;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.protocol.ChannelControlChangeConsoleMessage;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ChannelVPotMoved
extends ChannelControlChangeConsoleMessage
implements Signal {

    public final Channel channel;
    public final int velocity;

    public ChannelVPotMoved(final Channel channel, final int velocity) {
        super((byte) 0x00,
                (byte) (0x10 + channel.ordinal()),
                (byte) velocity);

        this.channel = channel;
        this.velocity = velocity;
    }

}
