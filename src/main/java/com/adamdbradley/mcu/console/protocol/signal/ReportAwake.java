package com.adamdbradley.mcu.console.protocol.signal;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Message;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ReportAwake
extends Message
implements Signal {

    public final DeviceType deviceType;

    /**
     * @param deviceType
     * @param unique An 11 word opaque identifier for the last time the MCU rebooted.
     */
    public ReportAwake(final DeviceType deviceType, final byte[] unique) {
        super(build(deviceType, unique));

        this.deviceType = deviceType;
    }

    private static MCUSysexMessage build(final DeviceType deviceType, final byte[] unique) {
        try {
            return new MCUSysexMessage(deviceType,
                    (byte) 0x01,
                    unique[0],
                    unique[1],
                    unique[2],
                    unique[3],
                    unique[4],
                    unique[5],
                    unique[6],
                    unique[7],
                    unique[8],
                    unique[9],
                    unique[10]);
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + ":" + MCUSysexMessage.toString(message);
    }
}
