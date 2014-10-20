package com.adamdbradley.mcu.console.protocol.command;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Message;

public class Reboot2
extends Message
implements Command {

    public final DeviceType deviceType;

    public Reboot2(final DeviceType deviceType) {
        super(build(deviceType));

        this.deviceType = deviceType;
    }

    private static MCUSysexMessage build(final DeviceType deviceType) {
        try {
            return new MCUSysexMessage(deviceType, (byte) 0x09);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

}
