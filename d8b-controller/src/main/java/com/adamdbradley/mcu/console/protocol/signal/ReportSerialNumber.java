package com.adamdbradley.mcu.console.protocol.signal;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ReportSerialNumber
implements Signal {

    private final SysexMessage message;

    public ReportSerialNumber(final DeviceType deviceType,
            final byte[] serialNumber) {
        final byte[] payload = new byte[7];
        payload[0] = 0x1B;
        System.arraycopy(serialNumber, 0, payload, 1, 6);

        try {
            this.message = new MCUSysexMessage(deviceType, payload);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
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
