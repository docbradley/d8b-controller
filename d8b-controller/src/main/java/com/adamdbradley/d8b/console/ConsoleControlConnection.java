package com.adamdbradley.d8b.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.adamdbradley.d8b.ControlConnection;
import com.adamdbradley.d8b.console.command.Command;
import com.adamdbradley.d8b.console.signal.ConsoleSignalParser;
import com.adamdbradley.d8b.console.signal.Signal;

import jssc.SerialPortException;

/**
 * Encapsulates communication with the console side of the d8b.
 */
public class ConsoleControlConnection
extends ControlConnection
implements AutoCloseable {

    public final ConsoleSignalParser parser = new ConsoleSignalParser();

    private final Thread parserThread;
    private final List<Queue<Signal>> subscriptions = Collections.synchronizedList(new ArrayList<>());

    private volatile boolean running;

    public ConsoleControlConnection(final String portName) throws SerialPortException {
        super(portName);

        running = true;

        parserThread = new Thread() {
            {
                setName("Console parser thread");
                setDaemon(true);
            }
            /*
             * Consume the #signalBuffer and turn it into parsed Signals,
             * then deliver them to subscriber queues.
             */
            public void run() {
                while (running) {
                    while (signalBuffer.length() > 0) {
                        final char character;
                        synchronized(signalBuffer) {
                            character = signalBuffer.charAt(0);
                            signalBuffer.deleteCharAt(0);
                        }
                        final Signal signal = parser.receiveCharacter((byte) character);
                        if (signal != null) {
                            enqueue(signal);
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                        return;
                    }
                }
            }

            private void enqueue(final Signal signal) {
                for (Queue<Signal> queue: subscriptions) {
                    queue.add(signal);
                }
            }
        };

        parserThread.start();
    }


    /**
     * Subscribe to the parsed {@link Signal} stream produced by
     * {@link #parserThread}.  Parsed {@link Signal}s are
     * {@link Queue#add(Object)}ed to all subscribed {@link Queue}s
     * until they are {@link #unsubscribe(Queue)}d.
     * <p>
     * Take care to use a {@link Queue} implementation that won't
     * block in {@link Queue#add(Object)}.
     * 
     * @param queue
     */
    public void subscribe(final Queue<Signal> queue) {
        this.subscriptions.add(queue);
    }

    /**
     * Stop receiving parsed {@link Signal}s on the {@link Queue}.
     * Note that this does not flush the queue for you.
     * @param queue
     */
    public void unsubscribe(final Queue<Signal> queue) {
        this.subscriptions.remove(queue);
    }


    @Override
    public void close() throws SerialPortException {
        this.running = false;
        super.close();
    }

    /**
     * Convenience method for sending one or more {@link Command}s
     * to the hardware over the serial line.
     * @param commands
     */
    public void send(final Command ... commands) {
        send(Arrays.asList(commands));
    }

    /**
     * Convenience method for sending one or more {@link Command}s
     * to the hardware over the serial line.
     * @param commands
     */
    public void send(final List<? extends Command> commands) {
        for (final Command command: commands) {
            final String serial = command.serialize();
            writeBytes(serial);
        }
    }

}
