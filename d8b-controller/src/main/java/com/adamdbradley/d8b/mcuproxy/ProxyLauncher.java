package com.adamdbradley.d8b.mcuproxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import com.adamdbradley.d8b.audio.AudioControlConnection;
import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.mcu.MCUEmulatorPort;

public class ProxyLauncher {

    public static void main(final String argv[]) throws Exception {
        final MCUEmulatorPort[] ports = findAndOpenMotuXT();
        final ConsoleControlConnection console = new ConsoleControlConnection("COM12");
        final AudioControlConnection audio = new AudioControlConnection("COM14");
        try (Proxy proxy = new Proxy(console, audio, ports[0], ports[1], ports[2])) {
            proxy.run();
        }
    }

    private static MCUEmulatorPort[] findAndOpenMotuXT() throws MidiUnavailableException {
        final List<MidiDevice.Info> infos = Arrays.asList(MidiSystem.getMidiDeviceInfo());

        final List<MidiDevice.Info> ins = new ArrayList<>(3);
        final List<MidiDevice.Info> outs = new ArrayList<>(3);

        for (final MidiDevice.Info info: infos) {
            if (info.getName().matches("Port [0-9]+ on MXPXT")) {
                if (MidiSystem.getMidiDevice(info).getMaxTransmitters() != 0) {
                    ins.add(info);
                } else {
                    outs.add(info);
                }
            }
        }

        final MCUEmulatorPort[] ports = new MCUEmulatorPort[3];
        for (int i=0; i<3; i++) {
            ports[i] = new MCUEmulatorPort(ins.get(i), outs.get(i));
        }

        return ports;
    }

}
