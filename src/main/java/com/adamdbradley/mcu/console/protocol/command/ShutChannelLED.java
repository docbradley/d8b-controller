package com.adamdbradley.mcu.console.protocol.command;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelLED;
import com.adamdbradley.mcu.console.protocol.Command;

public class ShutChannelLED
extends ChannelLEDCommandBase
implements Command {

    public ShutChannelLED(final Channel channel, final ChannelLED led) {
        super(channel, led, false);
    }

}
