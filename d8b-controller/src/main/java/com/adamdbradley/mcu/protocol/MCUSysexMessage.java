
package com.adamdbradley.mcu.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import com.adamdbradley.mcu.console.DeviceType;

public class MCUSysexMessage extends SysexMessage {

    public static final byte[] PREFIX = new byte[] { (byte) 0xf0, 0x00, 0x00, 0x66, 
        0x00 // Replace with deviceType encoding
        };

    public final DeviceType deviceType;

    public MCUSysexMessage(final DeviceType deviceType,
            final byte ... bytes) throws InvalidMidiDataException {
        super(build(deviceType, bytes), bytes.length + PREFIX.length + 1);
 
        this.deviceType = deviceType;
    }

    private static byte[] build(final DeviceType deviceType, final byte[] bytes) {
        final byte[] message = new byte[PREFIX.length + bytes.length + 1];
        for (int i=0; i<PREFIX.length; i++) {
            message[i] = PREFIX[i];
        }
        message[PREFIX.length - 1] = deviceType.encode();
        for (int i=0; i<bytes.length; i++) {
            message[PREFIX.length + i] = bytes[i];
        }
        message[PREFIX.length + bytes.length] = (byte) 0xF7;
        return message;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(this);
    }

    public static String toString(final MidiMessage message) {
        final StringBuilder sb = new StringBuilder();
        for (int i=0; i<message.getLength(); i++) {
            sb.append(Integer.toHexString(0x00FF & message.getMessage()[i])).append(' ');
        }
        return sb.toString();
    }
}
