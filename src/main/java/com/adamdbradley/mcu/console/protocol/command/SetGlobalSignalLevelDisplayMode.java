package com.adamdbradley.mcu.console.protocol.command;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.SignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Message;

public class SetGlobalSignalLevelDisplayMode
extends Message
implements Command {

    public final DeviceType deviceType;
    public final SignalLevelDisplayMode mode;

    /**
     * @param channel
     * @param mode
     * @throws InvalidMidiDataException
     */
    public SetGlobalSignalLevelDisplayMode(final DeviceType deviceType,
            final SignalLevelDisplayMode mode) {
        super(build(deviceType, mode));

        this.deviceType = deviceType;
        this.mode = mode;
    }

    private static MCUSysexMessage build(final DeviceType deviceType,
            final SignalLevelDisplayMode mode) {
        try {
            return new MCUSysexMessage(deviceType, (byte) 0x21, (byte) mode.ordinal());
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

}
