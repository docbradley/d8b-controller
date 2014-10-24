package com.adamdbradley.mcu.console.protocol.signal;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelButton;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ChannelButtonPressed
extends ChannelButtonMessage
implements Signal {

    public ChannelButtonPressed(final Channel channel, final ChannelButton channelButton) {
        super(channel, channelButton, true);
    }

}
