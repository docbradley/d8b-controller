package com.adamdbradley.d8b.audio;

import com.adamdbradley.d8b.ControlConnection;

import jssc.SerialPortException;

public class AudioControlConnection
extends ControlConnection
implements AutoCloseable {

    public AudioControlConnection(final String portName) throws SerialPortException {
        super(portName);
    }

}
