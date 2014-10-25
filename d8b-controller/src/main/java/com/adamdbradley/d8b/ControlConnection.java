package com.adamdbradley.d8b;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import com.adamdbradley.d8b.audio.AudioControlConnection;
import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.google.common.base.Charsets;

/**
 * Common class for encapsulating a serial line used to communicate
 * with the d8b hardware.  Note that there are two serial lines used
 * to communicate with the two parts of the d8b hardware:
 * {@link ConsoleControlConnection} to the "console" side
 * (see {@link com.adamdbradley.d8b.console}), and
 * {@link AudioControlConnection} to the "audio" side
 * (see {@link com.adamdbradley.d8b.audio}).
 * <p/>
 * The {@link ControlConnection} uses a {@link SerialPort} event
 * handler to read all inbound bytes into {@link #signalBuffer}, from
 * which they can be consumed.  In general it's a better idea to
 * consume higher-level parsed output provided by subclass mechanisms.
 */
public class ControlConnection
implements AutoCloseable {

    public final StringBuffer signalBuffer = new StringBuffer();

    private final SerialPort port;

    protected ControlConnection(final String portName)
            throws SerialPortException {
        port = openPort(portName);

        port.addEventListener(new SerialPortEventListener() {
            @Override
            public void serialEvent(final SerialPortEvent serialPortEvent) {
                switch (serialPortEvent.getEventType()) {
                case SerialPortEvent.RXCHAR:
                case SerialPortEvent.RXFLAG:
                    try {
                        final byte[] next = port.readBytes();
                        if (next != null && next.length > 0) {
                            signalBuffer.append(new String(next, Charsets.US_ASCII));
                        }
                    } catch (SerialPortException e) {
                        e.printStackTrace(System.err);
                    }
                    break;
                default:
                    System.err.println("Problem: " + serialPortEvent.getPortName()
                            + " " + serialPortEvent.getEventType()
                            + " " + serialPortEvent.getEventValue());
                    // Take no other action
                }
            }
        }, SerialPortEvent.RXCHAR | SerialPortEvent.RXFLAG | SerialPortEvent.ERR);
    }

    private SerialPort openPort(final String portName)
            throws SerialPortException {
        final SerialPort port = new SerialPort(portName);
        port.openPort();
        port.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, false, false);
        port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        return port;
    }


    @Override
    public void close() throws SerialPortException {
        // Let the hardware know it should reboot itself
        port.writeByte((byte) 'R');
        port.closePort();
    }



    /**
     * Convenience method for sending a String to the hardware
     * over the serial line.
     * If the String isn't ASCII-7 clean, behavior is undefined.
     * @param commands
     */
    public void writeBytes(String string) {
        writeBytes(string.getBytes(Charsets.US_ASCII));
    }

    /**
     * Send a sequence of bytes to the hardware over the serial line.
     * @param commands
     */
    public void writeBytes(byte[] bytes) {
        synchronized(port) {
            try {
                port.writeBytes(bytes);
            } catch (SerialPortException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Convenience method to send a single character to the hardware
     * over the serial line.  
     * If the character isn't ASCII-7 clean, behavior is undefined.
     * @param character
     */
    public void writeByte(char character) {
        writeByte((byte) character);
    }

    /**
     * Send a single byte to the hardware over the serial line.
     * @param character
     */
    public void writeByte(byte character) {
        try {
            port.writeByte(character);
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
    }

}
