package com.adamdbradley.mcu.console.protocol.signal;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelButton;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ChannelButtonReleased
extends ChannelButtonMessage
implements Signal {

    public ChannelButtonReleased(final Channel channel, final ChannelButton channelButton) {
        super(channel, channelButton, false);
    }

}
