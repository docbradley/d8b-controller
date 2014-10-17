package com.adamdbradley.mcu;

import java.util.Arrays;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;

public class MidiLister {

    public static void main(final String argv[]) throws Exception {
        final List<MidiDevice.Info> list = Arrays.asList(MidiSystem.getMidiDeviceInfo());
        for (MidiDevice.Info info: list) {
            final MidiDevice device = MidiSystem.getMidiDevice(info);
            System.out.println(info.getName() + " -- "
                    + info.getVendor() + " -- "
                    + info.getDescription() + " -- "
                    + info.getVersion() + " -- "
                    + device.getMaxReceivers() + ":"
                    + device.getMaxTransmitters());
        }
    }

}
