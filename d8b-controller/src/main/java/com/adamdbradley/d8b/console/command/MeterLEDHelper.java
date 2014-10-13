package com.adamdbradley.d8b.console.command;

import java.math.BigInteger;
import java.util.List;

import com.adamdbradley.d8b.console.MeterLEDChannel;
import com.adamdbradley.d8b.console.MeterLEDNumber;
import com.google.common.collect.ImmutableList;

/**
 * Convenience methods for manipulating the MeterLEDs.
 */
public class MeterLEDHelper {

    /**
     * @param meter
     * @param level can be <code>null</code> for "off"
     * @return
     */
    public static List<Command> buildFullMeterLevelCommands(MeterLEDChannel meter, MeterLEDNumber level) {
        final ImmutableList.Builder<Command> result = ImmutableList.<Command>builder();
        for (final MeterLEDNumber number: MeterLEDNumber.values()) {
            if (level != null && number.ordinal() <= level.ordinal()) {
                result.add(new LightMeterLED(meter, number));
            } else {
                result.add(new ShutMeterLED(meter, number));
            }
        }
        return result.build();
    }

    /**
     * @param meter
     * @param level is 0 (nothing), 1 (minimum) through 12 (full).
     * @return
     */
    public static List<Command> buildPointMeterLevelCommands(MeterLEDChannel meter, MeterLEDNumber level) {
        final ImmutableList.Builder<Command> result = ImmutableList.<Command>builder();
        for (final MeterLEDNumber number: MeterLEDNumber.values()) {
            if (level != null && number.ordinal() == level.ordinal()) {
                result.add(new LightMeterLED(meter, number));
            } else {
                result.add(new ShutMeterLED(meter, number));
            }
        }
        return result.build();
    }

    /**
     * @param meter
     * @param mask
     * @return
     */
    public static List<Command> buildMeterBitmaskCommands(MeterLEDChannel meter, MeterLEDMask mask) {
        final ImmutableList.Builder<Command> result = ImmutableList.<Command>builder();
        for (final MeterLEDNumber number: MeterLEDNumber.values()) {
            if (number != null && mask.check(number)) {
                result.add(new LightMeterLED(meter, number));
            } else {
                result.add(new ShutMeterLED(meter, number));
            }
        }
        return result.build();
    }

    public static class MeterLEDMask implements Cloneable {
        private BigInteger bitmask;

        public MeterLEDMask() {
            bitmask = BigInteger.ZERO;
        }
        public MeterLEDMask(long value) {
            bitmask = BigInteger.valueOf(value);
        }
        public MeterLEDMask(String value) {
            bitmask = new BigInteger(value);
        }
        public MeterLEDMask(BigInteger value) {
            bitmask = value;
        }

        public MeterLEDMask set(MeterLEDNumber ledNumber) {
            bitmask = bitmask.setBit(ledNumber.ordinal() - MeterLEDNumber.Level1.ordinal());
            return this;
        }

        public MeterLEDMask clear(MeterLEDNumber ledNumber) {
            bitmask = bitmask.clearBit(ledNumber.ordinal() - MeterLEDNumber.Level1.ordinal());
            return this;
        }

        public boolean check(MeterLEDNumber ledNumber) {
            return bitmask.testBit(ledNumber.ordinal() - MeterLEDNumber.Level1.ordinal());
        }
    }

}
