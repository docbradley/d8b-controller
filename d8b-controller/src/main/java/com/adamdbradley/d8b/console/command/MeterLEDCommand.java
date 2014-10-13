package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.ConsoleIdMaps;
import com.adamdbradley.d8b.console.MeterLEDChannel;
import com.adamdbradley.d8b.console.MeterLEDNumber;

/**
 * Helper for commands pertaining to MeterLEDs.
 * Encapsulates the mapping between our logical naming scheme
 * of {@link MeterLEDChannel}/{@link MeterLEDNumber} and the underlying
 * identifiers used by the console itself.
 */
abstract class MeterLEDCommand extends Command {

    private final MeterLEDChannel meter;
    private final MeterLEDNumber ledNumber;

    /**
     * 
     * @param commandType
     * @param meter 0-23 (channel), 24-25 (master L/R)
     * @param ledNumber 0-11 (0-7 green, 8-10 yellow, 11 red)
     */
    protected MeterLEDCommand(final CommandType commandType,
            final MeterLEDChannel meter, final MeterLEDNumber ledNumber) {
        super(commandType);
        this.meter = meter;
        this.ledNumber = ledNumber;
    }

    @Override
    public final String serializeParameters() {
        final int ledId = ConsoleIdMaps.meterLEDChannelLookup.get(meter).getLEDId(ledNumber);
        return toThreeDigitHexString(ledId);
    }

}
