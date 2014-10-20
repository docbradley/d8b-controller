package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

public class UniversalDeviceQuery
extends Message
implements Command {

    private static final byte[] PAYLOAD = new byte[] {
            (byte) 0xF0,
            0x7E,
            0x00,
            0x06,
            0x01,
            0x7F
    };

    public UniversalDeviceQuery()
            throws InvalidMidiDataException {
        super(new SysexMessage(PAYLOAD, PAYLOAD.length));
    }

}
