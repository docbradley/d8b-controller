package com.adamdbradley.mcu.actor;

import com.adamdbradley.mcu.MCUMidiPort;
import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelLED;
import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.PanelLED;
import com.adamdbradley.mcu.console.protocol.command.MoveFader;
import com.adamdbradley.mcu.console.protocol.command.ShutChannelLED;
import com.adamdbradley.mcu.console.protocol.command.ShutPanelLED;
import com.adamdbradley.mcu.console.protocol.command.WriteVPot;

/**
 * All commands are agnostic to device type, so this can be run against
 * a Master or Slave console.
 * <p/>
 * (I don't have the mappings set up for the C4 yet, so that's not covered.)
 */
public class MCUReset extends MCUActor {

    public MCUReset(final MCUMidiPort port) {
        super(port);
    }

    @Override
    public Void call() throws Exception {
        for (Fader fader: Fader.values()) {
            port.send(new MoveFader(fader, 0));
        }
        for (Channel channel: Channel.values()) {
            port.send(new ShutChannelLED(channel, ChannelLED.REC));
            port.send(new ShutChannelLED(channel, ChannelLED.SOLO));
            port.send(new ShutChannelLED(channel, ChannelLED.MUTE));
            port.send(new ShutChannelLED(channel, ChannelLED.SELECT));
            port.send(new WriteVPot(channel, null, 0, false));
        }
        for (PanelLED led: PanelLED.values()) {
            port.send(new ShutPanelLED(led));
        }
        return null;
    }

}
