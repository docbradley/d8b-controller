package com.adamdbradley.mcu.console.protocol.signal;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.protocol.ChannelControlChangeConsoleMessage;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ChannelVPotMoved
extends ChannelControlChangeConsoleMessage
implements Signal {

    public final Channel channel;
    public final int velocity;

    /**
     * Velocity is in [-15, -1] or [1, 15]
     * (I've only seen up to 14 in either direction, but just in case...)
     * Right move CC value is velocity
     * Left move CC value is 40 + abs(velocity)
     * @param channel
     * @param velocity
     */
    public ChannelVPotMoved(final Channel channel, final int velocity) {
        super((byte) 0x00,
                (byte) (0x10 + channel.ordinal()),
                (byte) ((velocity >= 0) ? velocity : 0x40 - velocity));

        this.channel = channel;
        this.velocity = velocity;
    }

}
