package com.adamdbradley.d8b.console;

import org.junit.Test;

import com.adamdbradley.d8b.console.command.LightVPotLED;

public class TestConsoleIdMaps {

    @Test
    public void smokeTest() {
        ConsoleIdMaps.faderLookup.toString();
        ConsoleIdMaps.meterLEDChannelLookup.toString();
        ConsoleIdMaps.panelButtonLookup.toString();
        ConsoleIdMaps.vpotLookup.toString();
    }

    @Test
    public void vpotLEDs() {
        for (VPot vpot: VPot.values()) {
            for (VPotLEDPosition position: VPotLEDPosition.values()) {
                new LightVPotLED(new VPotLED(vpot, position)).serialize();
            }
        }
    }
}
