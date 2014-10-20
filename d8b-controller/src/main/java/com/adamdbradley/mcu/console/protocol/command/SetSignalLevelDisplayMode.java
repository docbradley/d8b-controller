package com.adamdbradley.mcu.console.protocol.command;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.SignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Message;

public class SetSignalLevelDisplayMode
extends Message
implements Command {

    public final DeviceType deviceType;
    public final Channel channel;
    public final SignalLevelDisplayMode mode;

    /**
     * Should always be preceeded by {@link SetSignalLevel} to zero.
     * @param channel
     * @param mode
     * @throws InvalidMidiDataException
     */
    public SetSignalLevelDisplayMode(final DeviceType deviceType, 
            final Channel channel,
            final SignalLevelDisplayMode mode) {
        super(build(deviceType, channel, mode));

        this.deviceType = deviceType;
        this.channel = channel;
        this.mode = mode;
    }

    private static MCUSysexMessage build(final DeviceType deviceType,
            final Channel channel,
            final SignalLevelDisplayMode mode) {
        try {
            return new MCUSysexMessage(deviceType,
                    (byte) 0x20, (byte) channel.ordinal(), (byte) mode.ordinal());
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

}
