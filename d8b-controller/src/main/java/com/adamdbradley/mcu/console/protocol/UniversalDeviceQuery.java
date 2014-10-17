package com.adamdbradley.mcu.protocol;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

public class UniversalDeviceQuery
implements Command {

    private final MidiMessage message;

    public UniversalDeviceQuery()
            throws InvalidMidiDataException {
        final byte[] message = new byte[] {
                (byte) 0xF0,
                0x7E,
                0x00,
                0x06,
                0x01,
                0x7F
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
