package com.adamdbradley.mcu.console.protocol.command;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.SignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;

public class SetGlobalSignalLevelDisplayMode
implements Command {

    public final DeviceType deviceType;

    private final MCUSysexMessage message;

    /**
     * Should always be preceded by {@link SetSignalLevel} to zero.
     * @param channel
     * @param mode
     * @throws InvalidMidiDataException
     */
    public SetGlobalSignalLevelDisplayMode(final DeviceType deviceType,
            final SignalLevelDisplayMode mode) {
        try {
            message = new MCUSysexMessage(deviceType,
                    (byte) 0x21, (byte) mode.ordinal());
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
        this.deviceType = deviceType;
    }

    @Override
    public MidiMessage getMessage() {
        return message;
    }

}
