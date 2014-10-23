package com.adamdbradley.mcu.console.protocol.signal;

import java.math.BigInteger;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Message;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ReportSerialNumber
extends Message
implements Signal {

    private final BigInteger serialNumber;

    /**
     * @param deviceType
     * @param serialNumber (7 words)
     */
    public ReportSerialNumber(final DeviceType deviceType,
            final byte[] serialNumber) {
        super(build(deviceType, serialNumber));

        if (serialNumber.length != 7) {
            throw new IllegalArgumentException("SerialNumber should be 7 long, is " + serialNumber.length);
        }
        this.serialNumber = new BigInteger(serialNumber);
    }

    private static MCUSysexMessage build(final DeviceType deviceType,
            final byte[] serialNumber) {
        final byte[] payload = new byte[serialNumber.length + 1];
        payload[0] = 0x1B;
        System.arraycopy(serialNumber, 0, payload, 1, serialNumber.length);

        try {
            return new MCUSysexMessage(deviceType, payload);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(message)
                + " -- " + serialNumber.toString(16);
    }

}
