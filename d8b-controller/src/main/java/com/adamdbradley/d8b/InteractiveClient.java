package com.adamdbradley.d8b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.adamdbradley.d8b.actor.ConsoleActor;
import com.adamdbradley.d8b.actor.FullBoot;
import com.adamdbradley.d8b.actor.Reset;
import com.adamdbradley.d8b.actor.TryAllCommands;
import com.adamdbradley.d8b.actor.Vegas;
import com.adamdbradley.d8b.audio.AudioControlConnection;
import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.d8b.console.signal.Signal;
import com.google.common.base.Charsets;

import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * Simple interactive d8b client.
 * After booting up the console and audio sections, users can:
 * <ul>
 * <li>Send commands directly by prefixing them with ':'
 * <li>Invoke actors registered in the {@link #namedActors} map
 * <li>Quit gracefully (this notifies the d8b hardware to reboot itself)
 * </ul>
 * The client also prints any messages it receives on {@link System#out}.
 */
public class InteractiveClient implements Runnable, AutoCloseable {

    private final ConsoleControlConnection console1;
    private final AudioControlConnection console2;
    private final Queue<Signal> signalPrinterQueue = new ConcurrentLinkedQueue<>();

    private final Map<String, ConsoleActor> namedActors = new ConcurrentHashMap<>();


    private final Thread signalPrinter = new Thread() {
        {
            setDaemon(true);
            setName("SignalPrinter");
        }
        public void run() {
            final ConsoleStatusMap consoleStatusMap = new ConsoleStatusMap();
            while (true) {
                try {
                    final Signal signal = signalPrinterQueue.poll();
                    if (signal != null) {
                        if (consoleStatusMap.isUpdate(signal)) {
                            report(signal);
                        }
                    } else {
                        // TODO: Could use a LinkedBlockingQueue
                        // so we could block instead of polling?
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        private void report(final Signal signal) {
            switch (signal.type) {
            case Heartbeat1:
            case Heartbeat2:
                // The two heartbeat messages aren't worth printing
                return;

            default:
                System.out.println(signal);
            }
        }
    };

    public InteractiveClient(final String argv[]) throws SerialPortException {
        final List<String> availableSerialPorts = Arrays.asList(SerialPortList.getPortNames());
        final String[] portsToUse;

        if (argv.length == 2) {
            portsToUse = argv;
        } else {
            portsToUse = new String[] { "COM12", "COM14" };
        }

        if (!availableSerialPorts.contains(portsToUse[0])
                || !availableSerialPorts.contains(portsToUse[1])) {
            System.out.println("USAGE: " + this.getClass().getSimpleName() + " <ConsoleSerialPort> <AudioSerialPort>");
            System.out.println("Recognized ports: " + Arrays.asList(SerialPortList.getPortNames()));
            throw new IllegalArgumentException("Must specify serial ports");
        }

        console1 = new ConsoleControlConnection(portsToUse[0]);
        console2 = new AudioControlConnection(portsToUse[1]);
    }

    public void close() throws Exception {
        try {
            console1.close();
        } finally {
            console2.close();
        }
    }

    @Override
    public void run() {
        console1.subscribe(signalPrinterQueue);
        signalPrinter.start();

        try {
            final FullBoot boot = new FullBoot(console1, console2);
            boot.call();

            final Reset reset = new Reset(console1);
            reset.call();

            namedActors.put("reboot", boot);
            namedActors.put("reset", reset);
            namedActors.put("diagnostic", new TryAllCommands(console1));
            namedActors.put("vegas", new Vegas(console1, Duration.ofSeconds(60)));

            runConsoleClient();
        } catch (InterruptedException | SerialPortException | IOException e) {
            throw new RuntimeException("Failed", e);
        }
    }

    private void runConsoleClient() throws SerialPortException, IOException {
        System.out.println("Enter '?' for help");
        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = console.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                switch(line.charAt(0)) {
                case '?': // Help
                    System.err.println(""
                            + "?      - HELP\n"
                            + "[NAME] - Run a console actor: " + namedActors.keySet() + "\n"
                            + "q      - QUIT");
                    break;
                case ':':
                    console1.writeBytes(line.substring(1).getBytes(Charsets.US_ASCII));
                    System.out.println("Sent");
                    break;
                case 'q': // Quit
                    System.out.println("Exiting");
                    return;
                default:
                    if (namedActors.containsKey(line.toLowerCase().trim())) {
                        try {
                            namedActors.get(line.toLowerCase().trim()).call();
                            System.out.println("Done");
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        }
                    } else {
                        System.err.println("Unknown command");
                    }
                }
            }
        }
    }


    public static void main(final String[] argv) throws Exception {
        try (InteractiveClient client = new InteractiveClient(argv)) {
            client.run();
        }
    }

}
