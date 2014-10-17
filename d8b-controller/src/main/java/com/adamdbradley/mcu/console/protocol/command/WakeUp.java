package com.adamdbradley.mcu.console.protocol.command;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;

public class WakeUp
implements Command {

    public final DeviceType deviceType;

    private final MCUSysexMessage message;

    public WakeUp(final DeviceType deviceType) {
        try {
            message = new MCUSysexMessage(deviceType,
                    (byte) 0x00);
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
