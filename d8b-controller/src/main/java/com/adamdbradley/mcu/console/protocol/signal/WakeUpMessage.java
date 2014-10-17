package com.adamdbradley.mcu.console.protocol.signal;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Signal;

public class WakeUpMessage
implements Signal {

    public final DeviceType deviceType;

    private MCUSysexMessage message;

    public WakeUpMessage(final DeviceType deviceType, final byte[] unique) {
        try {
            this.message = new MCUSysexMessage(deviceType,
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
        this.deviceType = deviceType;
    }

    @Override
    public MidiMessage getMessage() {
        return message;
    }

}
