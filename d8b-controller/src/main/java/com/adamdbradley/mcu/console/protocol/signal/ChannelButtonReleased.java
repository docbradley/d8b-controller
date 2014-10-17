package com.adamdbradley.mcu.console.protocol.signal;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelButton;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ChannelButtonReleased
extends ChannelButtonMessage
implements Signal {

    public ChannelButtonReleased(final Channel channel, final ChannelButton channelButton)
            throws InvalidMidiDataException {
        super(channel, channelButton, false);
    }

}
