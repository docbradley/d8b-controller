package com.adamdbradley.mcu.console.protocol.signal;

import java.math.BigInteger;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Signal;
import com.adamdbradley.mcu.console.protocol.command.RequestVersion;

/**
 * Reply to {@link RequestVersion}
 */
public class ReportVersion
implements Signal {

    private final BigInteger version;
    private final SysexMessage message;

    public ReportVersion(final DeviceType deviceType,
            final byte[] versionNumber) {
        try {
            this.message = new MCUSysexMessage(deviceType,
                    (byte) 0x14,
                    versionNumber[0],
                    versionNumber[1],
                    versionNumber[2],
                    versionNumber[3],
                    versionNumber[4]);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
        this.version = new BigInteger(versionNumber);
    }

    @Override
    public MidiMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + MCUSysexMessage.toString(message)
                + " -- " + version.toString(16);
    }

}
