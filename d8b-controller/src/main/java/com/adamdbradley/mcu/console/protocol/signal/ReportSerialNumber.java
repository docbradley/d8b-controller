package com.adamdbradley.mcu.console.protocol.signal;

import java.math.BigInteger;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ReportSerialNumber
implements Signal {

    private final BigInteger serialNumber;
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

        this.serialNumber = new BigInteger(serialNumber);
    }

    @Override
    public MidiMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(message)
                + " -- " + serialNumber.toString(16);
    }

}
