package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.ConsoleIdMaps;
import com.adamdbradley.d8b.console.PanelLED;

/**
 * Helper for commands pertaining to PanelLEDs.
 */
abstract class PanelLEDCommand extends Command {

    private final PanelLED led;

    /**
     * 
     * @param commandType
     * @param meter 0-23 (channel), 24-25 (master L/R)
     * @param ledNumber 0-11 (0-7 green, 8-10 yellow, 11 red)
     */
    protected PanelLEDCommand(final CommandType commandType,
            final PanelLED led) {
        super(commandType);
        this.led = led;
    }

    @Override
    public final String serializeParameters() {
        return toThreeDigitHexString(ConsoleIdMaps.panelLEDLookup.get(led));
    }

}
