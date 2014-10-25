package com.adamdbradley.mcu.console.protocol.signal;

import java.math.BigInteger;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Message;
import com.adamdbradley.mcu.console.protocol.Signal;
import com.adamdbradley.mcu.console.protocol.command.RequestVersion;

/**
 * Reply to {@link RequestVersion}
 */
public class ReportVersion
extends Message
implements Signal {

    private final BigInteger version;

    /**
     * 
     * @param deviceType
     * @param versionNumber (5 words)
     */
    public ReportVersion(final DeviceType deviceType,
            final byte[] versionNumber) {
        super(build(deviceType, versionNumber));

        this.version = new BigInteger(versionNumber);
    }

    private static MCUSysexMessage build(final DeviceType deviceType,
            final byte[] versionNumber) {
        try {
            return new MCUSysexMessage(deviceType,
                    (byte) 0x14,
                    versionNumber[0],
                    versionNumber[1],
                    versionNumber[2],
                    versionNumber[3],
                    versionNumber[4]);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(message)
                + " -- " + version.toString(16);
    }

}
