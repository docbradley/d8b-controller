package com.adamdbradley.mcu.console.protocol.signal;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelButton;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ChannelButtonPressed
extends ChannelButtonMessage
implements Signal {

    public ChannelButtonPressed(final Channel channel, final ChannelButton channelButton)
            throws InvalidMidiDataException {
        super(channel, channelButton, true);
    }

}
