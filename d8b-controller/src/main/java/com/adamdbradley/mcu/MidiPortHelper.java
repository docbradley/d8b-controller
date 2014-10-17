package com.adamdbradley.mcu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class MidiPortHelper {

    public static MCUMidiPort findAndOpenPort() throws MidiUnavailableException {
        final List<MidiDevice.Info> infos = Arrays.asList(MidiSystem.getMidiDeviceInfo());
        final List<MidiDevice.Info> candidates = new ArrayList<>(2);
        for (final MidiDevice.Info info: infos) {
            if (info.getName().startsWith("MCU Pro USB ")) {
                candidates.add(info);
            }
        }

        if (candidates.size() != 2) {
            throw new IllegalStateException("Didn't find what I'm looking for: " + infos);
        }

        return openPort(candidates);
    }

    private static MCUMidiPort openPort(final List<Info> candidates) throws MidiUnavailableException {
        if (MidiSystem.getMidiDevice(candidates.get(0)).getMaxReceivers() != 0) {
            return new MCUMidiPort(candidates.get(1), candidates.get(0));
        } else {
            return new MCUMidiPort(candidates.get(0), candidates.get(1));
        }
    }

}
