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
     * @param serialNumber (6 words)
     */
    public ReportSerialNumber(final DeviceType deviceType,
            final byte[] serialNumber) {
        super(build(deviceType, serialNumber));

        this.serialNumber = new BigInteger(serialNumber);
    }

    private static MCUSysexMessage build(final DeviceType deviceType,
            final byte[] serialNumber) {
        final byte[] payload = new byte[7];
        payload[0] = 0x1B;
        System.arraycopy(serialNumber, 0, payload, 1, 6);

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
