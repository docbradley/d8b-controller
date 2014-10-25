package com.adamdbradley.mcu.console.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

public class UniversalDeviceResponse
extends Message
implements Signal {

    public UniversalDeviceResponse(final byte[] deviceId, final byte[] version)
            throws InvalidMidiDataException {
        super(build(deviceId, version));
    }

    private static SysexMessage build(final byte[] deviceId, final byte[] version) {
        final byte[] payload = new byte[] {
                (byte) 0xF0,
                0x7E,
                0x00,
                0x06,
                0x02,
                0x00,
                0x00,
                0x66, // "Mackie"
                0x01,
                0x00,
                deviceId[0],
                deviceId[1],
                version[0],
                version[1],
                version[2],
                version[3],
                (byte) 0xF7
        };
        try {
            return new SysexMessage(payload, payload.length);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

}
