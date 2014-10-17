package com.adamdbradley.mcu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.adamdbradley.mcu.console.protocol.Signal;
import com.adamdbradley.mcu.console.protocol.signal.SignalParser;

/**
 * Encapsulates the two MIDI ports (one IN, one OUT) used to communicate
 * with an MCU.  To handle {@link Signal}s produced by the MCU surface,
 * {@link #subscribe(Queue)}.  To send {@link Command}s to the MCU surface,
 * call {@link #send(Command)}.
 */
public class MCUMidiPort
implements AutoCloseable {

    // Communications
    private final MidiDevice inDevice;
    private final Transmitter midiIn;
    private final MidiDevice outDevice;
    private final Receiver midiOut;

    // Handling input from the MCU
    private final SignalParser parser = new SignalParser();
    private final List<Queue<Signal>> subscribers = Collections
            .synchronizedList(new ArrayList<>(16));

    public MCUMidiPort(final MidiDevice.Info inDeviceInfo, final MidiDevice.Info outDeviceInfo)
            throws MidiUnavailableException {
        this(MidiSystem.getMidiDevice(inDeviceInfo), MidiSystem.getMidiDevice(outDeviceInfo));
    }

    public MCUMidiPort(final MidiDevice inDevice, final MidiDevice outDevice)
            throws MidiUnavailableException {
        if (inDevice.isOpen()) {
            throw new IllegalStateException("inDevice already open");
        }
        if (inDevice.getMaxTransmitters() == 0) {
            throw new IllegalArgumentException("Can't read from inDevice");
        }

        if (outDevice.isOpen()) {
            throw new IllegalStateException("outDevice already open");
        }
        if (outDevice.getMaxReceivers() == 0) {
            throw new IllegalArgumentException("Can't write to outDevice");
        }

        this.inDevice = inDevice;
        this.midiIn = inDevice.getTransmitter();
        this.midiIn.setReceiver(new Receiver() {
            @Override
            public void send(final MidiMessage message, final long timeStamp) {
                final Signal signal;
                try {
                    signal = parser.parse(message);
                } catch (InvalidMidiDataException e) {
                    System.err.println("Couldn't parse " + MCUSysexMessage.toString(message));
                    e.printStackTrace(System.err);
                    return;
                }

                if (signal != null) {
                    for (final Queue<Signal> subscriber: subscribers) {
                        subscriber.add(signal);
                    }
                } else {
                    System.out.println("Unknown message "
                            + MCUSysexMessage.toString(message));
                }
            }

            @Override
            public void close() {
                // Nothing to do
            }
        });

        this.outDevice = outDevice;
        this.midiOut = outDevice.getReceiver();

        this.outDevice.open();
        this.inDevice.open();
    }



    @Override
    public void close() {
        try {
            try {
                this.midiIn.close();
            } finally {
                if (inDevice.isOpen()) {
                    inDevice.close();
                }
            }
        } finally {
            try {
                this.midiOut.close();
            } finally {
                if (outDevice.isOpen()) {
                    outDevice.close();
                }
            }
        }
    }


    /**
     * Send a message to the MCU device
     * @param message
     */
    public void send(final Command command) {
        midiOut.send(command.getMessage(), -1);
    }

    /**
     * Subscribe to {@link Signal}s coming from the MCU device.
     * Be sure to use a {@link Queue} implementation whose
     * {@link Queue#add(Object)} implementation won't block.
     * @param mcuQueue
     */
    public void subscribe(final Queue<Signal> mcuQueue) {
        this.subscribers.add(mcuQueue);
    }

    /**
     * Unsubscribe to {@link Signal}s coming from the MCU device.
     * @param mcuQueue
     */
    public void unsubscribe(final Queue<Signal> mcuQueue) {
        this.subscribers.remove(mcuQueue);
    }

}
