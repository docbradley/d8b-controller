package com.adamdbradley.mcu.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

/**
 * 
 */
public class UniversalDeviceResponse
implements Signal {

    private final MidiMessage message;

    public UniversalDeviceResponse(final byte[] deviceId, final byte[] version)
            throws InvalidMidiDataException {
        final byte[] message = new byte[] {
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
        this.message = new SysexMessage(message, message.length);
    }

    @Override
    public MidiMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(message);
    }

}
