package com.adamdbradley.mcu.actor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.adamdbradley.mcu.MCUMidiPort;
import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.SignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.Signal;
import com.adamdbradley.mcu.console.protocol.UniversalDeviceQuery;
import com.adamdbradley.mcu.console.protocol.command.RequestSerialNumber;
import com.adamdbradley.mcu.console.protocol.command.SetGlobalSignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.command.WriteScreen;
import com.adamdbradley.mcu.demo.smoketest.MasterSmokeTest;
import com.google.common.base.Strings;

/**
 * Some commands are particular to "Master" devices.
 */
public class MasterStartup extends MCUActor {

    private final Queue<Signal> queue = new ConcurrentLinkedQueue<>();

    public MasterStartup(final MCUMidiPort port) {
        super(port);
    }

    @Override
    public Void call() throws Exception {
        port.subscribe(queue);

        port.send(new UniversalDeviceQuery());
        port.send(new RequestSerialNumber(DeviceType.Master));

        Thread.sleep(1000);

        port.send(new SetGlobalSignalLevelDisplayMode(DeviceType.Master,
                SignalLevelDisplayMode.OFF));
        port.send(new WriteScreen(DeviceType.Master,
                0, 0, 
                Strings.repeat("        ", 14)
                ));
        port.send(new WriteScreen(DeviceType.Master,
                0, 0x38 - 1 - MasterSmokeTest.MESSAGE.length(),
                MasterSmokeTest.MESSAGE));

        Thread.sleep(1000);

        while (!queue.isEmpty()) {
            System.err.println("MCU STARTUP: " + queue.poll());
        }

        port.unsubscribe(queue);

        return null;
    }

}
