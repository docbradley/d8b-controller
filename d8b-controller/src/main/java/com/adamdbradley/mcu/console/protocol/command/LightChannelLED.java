package com.adamdbradley.mcu.console.protocol.command;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelLED;
import com.adamdbradley.mcu.console.protocol.Command;

public class LightChannelLED
extends ChannelLEDCommandBase
implements Command {

    public LightChannelLED(final Channel channel, final ChannelLED led) {
        super(channel, led, true);
    }

}
