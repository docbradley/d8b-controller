package com.adamdbradley.d8b.actor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.time.Instant;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.adamdbradley.d8b.AppClock;
import com.adamdbradley.d8b.audio.AudioControlConnection;
import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.d8b.console.command.RequestSerialNumber;
import com.adamdbradley.d8b.console.command.ScreenHelper;
import com.adamdbradley.d8b.console.signal.SerialNumber;
import com.adamdbradley.d8b.console.signal.Signal;
import com.adamdbradley.d8b.console.signal.SignalType;
import com.google.common.base.Strings;

public class FullBoot
extends ConsoleActor
implements Callable<Void> {

    private final byte[] surfaceFirmwareImage;
    private final byte[] masterFirmwareImage;
    private final byte[] slaveFirmwareImage;
    private final byte[] audioConfigImage;

    private volatile boolean screenReady = false;

    public FullBoot(final ConsoleControlConnection console,
            final AudioControlConnection audio) {
        super(console, audio);

        surfaceFirmwareImage = readFirmware("./firmware/control.asc");
        masterFirmwareImage = readFirmware("./firmware/master.asc");
        slaveFirmwareImage = readFirmware("./firmware/slave.asc");
        audioConfigImage = readFirmware("./firmware/Config.asc");
    }

    private byte[] readFirmware(final String filename) {
        final File firmwareFile = new File(filename);
        try (FileInputStream fis = new FileInputStream(firmwareFile)) {
            final byte[] firmwareBuffer = new byte[65535];
            final int firmwareSize;
            firmwareSize = fis.read(firmwareBuffer);
            return Arrays.copyOf(firmwareBuffer, firmwareSize);
        } catch (IOException e) {
            throw new RuntimeException("Can't read firmware file", e);
        }
    }

    @Override
    public Void call() throws InterruptedException {
        handshakeAndSendFirmware();
        Thread.sleep(1000);

        // Blank the screen... 2 X 40
        console.writeBytes("01u");
        Thread.sleep(50);
        console.send(ScreenHelper.buildDrawScreenCommands(0, 0, "Controller Platform d8b-controller 0.0.0"));
        console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "(c)2014 Adam D. Bradley, All Rights Rsvd"));

        return null;
    }





    private void handshakeAndSendFirmware() throws InterruptedException {
        final AtomicBoolean failure = new AtomicBoolean(false);
        final UncaughtExceptionHandler failHandler = new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                failure.set(true);
                e.printStackTrace(System.err);
            }
        };

        final Thread t1 = new Thread() {
            {
                setUncaughtExceptionHandler(failHandler);
            }
            public void run() {
                bootstrapConsole();
            }
        };

        final Thread t2 = new Thread() {
            {
                setUncaughtExceptionHandler(failHandler);
            }
            public void run() {
                bootstrapAudio();
            }
        };

        dualHandshakes();

        t1.start();
        t2.start();

        while (t1.isAlive() || t2.isAlive()) {
            Thread.sleep(250);
        }

        if (failure.get()) {
            throw new RuntimeException("Couldn't bootstrap");
        }
    }

    private void dualHandshakes() throws InterruptedException {
        for (int i=0; i<12; i++) {
            audio.writeByte('0');
            console.writeByte('0');
            Thread.sleep(1000);

            System.err.println("Sending Rs");
            audio.signalBuffer.setLength(0);
            console.parser.clearAlive();

            audio.writeByte('R');
            console.writeByte('R');

            final Instant deadline = AppClock.now().plusSeconds(20);
            while (deadline.isAfter(AppClock.now())) {
                if (console.parser.isAlive()
                        && audio.signalBuffer.length() > 0
                        && audio.signalBuffer.charAt(0) == 'R') {
                    audio.signalBuffer.setLength(0);
                    return;
                } else {
                    if (console.parser.isAlive()) {
                        System.err.println("Console ack'd");
                    } else if (audio.signalBuffer.length() > 0) {
                        System.err.println("Audio signalbuffer " + audio.signalBuffer);
                    }
                    Thread.sleep(900);
                }
            }
        }
        throw new RuntimeException("Couldn't handshake");
    }


    private void bootstrapConsole() {
        final Queue<Signal> queue = new ConcurrentLinkedQueue<>();

        console.subscribe(queue);

        try {
            System.err.println("1: Sending firmware");
            console.parser.clearAlive();
            console.writeBytes(surfaceFirmwareImage);
            System.err.println("1: Sent firmware");

            // Wait for "R" ACK
            final Instant deadline = AppClock.now().plusSeconds(10);
            while (!console.parser.isAlive()
                    && deadline.isAfter(AppClock.now())) {
                Thread.sleep(10);
            }

            if (console.parser.isAlive()) {
                System.err.println("1: ACK");
            } else {
                throw new RuntimeException("FAIL");
            }

            System.err.println("Initializing screen");

            // Blank the screen... 2 X 40
            console.writeBytes("01u");
            Thread.sleep(50);
            console.send(ScreenHelper.buildDrawScreenCommands(0, 0, "                                        "));
            console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "                                        "));

            screenReady = true;

            console.send(ScreenHelper.buildDrawScreenCommands(0, 0, "BOOTING"));

            console.send(ScreenHelper.buildDrawScreenCommands(0, 10, "80p"));
            console.writeBytes("80p");

            Thread.sleep(200);

            console.send(ScreenHelper.buildDrawScreenCommands(0, 15, "81p"));
            console.writeBytes("81p");

            Thread.sleep(200);

            console.send(ScreenHelper.buildDrawScreenCommands(0, 20, "82p"));
            console.writeBytes("82p");

            Thread.sleep(200);

            console.send(ScreenHelper.buildDrawScreenCommands(0, 25, "83p"));
            console.writeBytes("83p");

            Thread.sleep(200);

            console.send(ScreenHelper.buildDrawScreenCommands(0, 30, "84p"));
            console.writeBytes("84p");

            Thread.sleep(200);

            console.send(ScreenHelper.buildDrawScreenCommands(0, 35, "80o"));
            console.writeBytes("80o");

            Thread.sleep(200);

            console.send(new RequestSerialNumber());
            final Instant serialNumberDeadline = AppClock.now().plusSeconds(10);
            do {
                Thread.sleep(100);
                for (Signal signal: queue) {
                    if (signal.type == SignalType.SerialNumber) {
                        console.send(ScreenHelper.buildDrawScreenCommands(0, 10, "                              "));
                        final String rawSN = ((SerialNumber) signal).serialNumber.toString(16);
                        final String printableSN = Strings.padStart(rawSN, 12, '0');
                        console.send(ScreenHelper.buildDrawScreenCommands(0, 10,
                                "Serial #: " + printableSN));
                        return;
                    } else {
                        console.send(ScreenHelper.buildDrawScreenCommands(0, 10,
                                "Waiting for Serial Number     "));
                    }
                }
            } while (serialNumberDeadline.isAfter(AppClock.now()));

            console.send(ScreenHelper.buildDrawScreenCommands(0, 10,
                    "No Serial Number Reported"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            console.unsubscribe(queue);
        }
    }

    private void bootstrapAudio() {
        try {
            // Send master.asc
            if (screenReady) {
                console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "Audio: Sending 1/4       "));
            }
            audio.writeBytes(masterFirmwareImage);

            if (screenReady) {
                console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "Audio: Waiting for ACK   "));
            }
            waitForAudioAck();

            if (screenReady) {
                console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "Audio: Sending 2/4       "));
            }
            audio.writeBytes(slaveFirmwareImage);
            audio.writeBytes(new byte[] { 0xD, 0xD });

            if (screenReady) {
                console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "Audio: Sending 3/4       "));
            }
            audio.writeBytes(audioConfigImage);
            audio.writeBytes(audioConfigImage);
            audio.writeBytes(new byte[] { 0xD, 0xD });

            if (screenReady) {
                console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "Audio: Sending 4/4       "));
            }
            Thread.sleep(300);
            System.err.println("2: Setting up(?)");
            audio.writeBytes("7X2$0V3$0V8X2$0V3$0V9X2$0V3$0VAX2$0V3$0VBX2$0V3$0VCX2$0V3$0V1X2"
                    + "$0V3$0V2X2$0V3$0V3X2$0V3$0V4X2$0V3$0V5X2$0V3$0V6X2$0V3$0V15X2$1"
                    + "V3$1V16X2$1V3$1V17X2$1V3$1V18X2$1V3$1V11X2$1V3$1V12X2$1V3$1V13X"
                    + "2$1V3$1V14X2$1V3$1VDX2$1V3$1VEX2$1V3$1VFX2$1V3$1V10X2$1V3$1V");

            if (screenReady) {
                console.send(ScreenHelper.buildDrawScreenCommands(1, 0, "Audio: READY             "));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean waitForAudioAck() throws InterruptedException {
        final Instant deadline = AppClock.now().plusSeconds(10);
        while (audio.signalBuffer.length() == 0
                && deadline.isAfter(AppClock.now())) {
            Thread.sleep(10);
        }
        if (audio.signalBuffer.length() > 0
                && audio.signalBuffer.charAt(0) == 'R') {
            System.err.println("2: ACK");
            return true;
        } else {
            return false;
        }
    }

}
