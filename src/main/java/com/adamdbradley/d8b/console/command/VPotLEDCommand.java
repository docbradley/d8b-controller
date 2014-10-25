package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.VPot;
import com.adamdbradley.d8b.console.VPotLED;
import com.adamdbradley.d8b.console.VPotLEDPosition;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

/**
 * Base class for {@link Command}s that manipulate VPot LEDs.
 */
class VPotLEDCommand extends Command {

    private final VPotLED led;

    /**
     * 
     * @param commandType
     * @param meter 0-23 (channel), 24-25 (master L/R)
     * @param ledNumber 0-11 (0-7 green, 8-10 yellow, 11 red)
     */
    protected VPotLEDCommand(final CommandType commandType,
            final VPotLED led) {
        super(commandType);
        this.led = led;
    }

    @Override
    public final String serializeParameters() {
        return toThreeDigitHexString(vpotLEDLookup.get(led));
    }

    private static final ImmutableBiMap<VPotLED, Integer> vpotLEDLookup;
    static {
        final ImmutableBiMap.Builder<VPotLED, Integer> builder = ImmutableBiMap.builder();
        for (final VPot vpot: VPot.values()) {
            switch (vpot) {
            case Ch1:
                new VPotLEDNumbers(vpot, 0x113).populate(builder);
                break;
            case Ch2:
                new VPotLEDNumbers(vpot, 0x10B, 5, 0x0F0).populate(builder);
                break;
            case Ch3:
                new VPotLEDNumbers(vpot, 0x0E3).populate(builder);
                break;
            case Ch4:
                new VPotLEDNumbers(vpot, 0x0DB, 5, 0x0C0).populate(builder);
                break;
            case Ch5:
                new VPotLEDNumbers(vpot, 0x0B3).populate(builder);
                break;
            case Ch6:
                new VPotLEDNumbers(vpot, 0x0AB, 5, 0x090).populate(builder);
                break;
            case Ch7:
                new VPotLEDNumbers(vpot, 0x083).populate(builder);
                break;
            case Ch8:
                new VPotLEDNumbers(vpot, 0x07B, 5, 0x060).populate(builder);
                break;
            case Ch9:
                new VPotLEDNumbers(vpot, 0x053).populate(builder);
                break;
            case Ch10:
                new VPotLEDNumbers(vpot, 0x04B, 5, 0x030).populate(builder);
                break;
            case Ch11:
                new VPotLEDNumbers(vpot, 0x023).populate(builder);
                break;
            case Ch12:
                new VPotLEDNumbers(vpot, 0x01B, 5, 0x000).populate(builder);
                break;
            case Ch13:
                new VPotLEDNumbers(vpot, 0x230 + 0x113).populate(builder);
                break;
            case Ch14:
                new VPotLEDNumbers(vpot, 0x230 + 0x10B, 5, 0x230 + 0x0F0).populate(builder);
                break;
            case Ch15:
                new VPotLEDNumbers(vpot, 0x230 + 0x0E3).populate(builder);
                break;
            case Ch16:
                new VPotLEDNumbers(vpot, 0x230 + 0x0DB, 5, 0x230 + 0x0C0).populate(builder);
                break;
            case Ch17:
                new VPotLEDNumbers(vpot, 0x230 + 0x0B3).populate(builder);
                break;
            case Ch18:
                new VPotLEDNumbers(vpot, 0x230 + 0x0AB, 5, 0x230 + 0x090).populate(builder);
                break;
            case Ch19:
                new VPotLEDNumbers(vpot, 0x230 + 0x083).populate(builder);
                break;
            case Ch20:
                new VPotLEDNumbers(vpot, 0x230 + 0x07B, 5, 0x230 + 0x060).populate(builder);
                break;
            case Ch21:
                new VPotLEDNumbers(vpot, 0x230 + 0x053).populate(builder);
                break;
            case Ch22:
                new VPotLEDNumbers(vpot, 0x230 + 0x04B, 5, 0x230 + 0x030).populate(builder);
                break;
            case Ch23:
                new VPotLEDNumbers(vpot, 0x230 + 0x023).populate(builder);
                break;
            case Ch24:
                new VPotLEDNumbers(vpot, 0x230 + 0x01B, 5, 0x230 + 0x000).populate(builder);
                break;
            case Master:
                new VPotLEDNumbers(vpot, 0x21E, 2, 0x210, 8, 0x228).populate(builder);
                break;
            case Low:
                new VPotLEDNumbers(vpot, 0x4B1, 7, 0x4C8).populate(builder);
                break;
            case LowMid:
                new VPotLEDNumbers(vpot, 0x4CD, 3, 0x4C0, 8, 0x4D8).populate(builder);
                break;
            case HiMid:
                new VPotLEDNumbers(vpot, 0x4D9, 7, 0x4D0).populate(builder);
                break;
            case Hi:
                new VPotLEDNumbers(vpot, 0x4D5, 3, 0x4E8, 8, 0x4E0).populate(builder);
                break;
            case SpeakerLevel:
                new VPotLEDNumbers(vpot, 0x4E6, 2, 0x4F8, 8, 0x4F0).populate(builder);
                break;
            case SoloStudioLevel:
                new VPotLEDNumbers(vpot, 0x480, 8, 0x498).populate(builder);
                break;
            case PhonesCueMix1Level:
                new VPotLEDNumbers(vpot, 0x493, 5, 0x4A8).populate(builder);
                break;
            case PhonesCueMix2Level:
                new VPotLEDNumbers(vpot, 0x4A3, 5, 0x4B8).populate(builder);
                break;
            default:
                throw new IllegalStateException("Unknown " + vpot);
            }
        }
        vpotLEDLookup = builder.build();
    }

    private static class VPotLEDNumbers {
        final VPot vpot;
        final int leftBase;
        final int leftSize;
        final int nextBase;
        final int nextSize;
        final int lastBase;

        VPotLEDNumbers(final VPot vpot, final int everythingBase) {
            this(vpot, everythingBase, VPotLEDPosition.values().length, 0xFFFF);
        }

        VPotLEDNumbers(final VPot vpot, final int everythingBase, final int leftSize, final int nextBase) {
            this(vpot, everythingBase, leftSize, nextBase, VPot.values().length - leftSize, 0xFFFF);
        }

        VPotLEDNumbers(final VPot vpot, final int leftBase, final int leftSize,
                final int nextBase, final int nextSize,
                final int lastBase) {
            this.vpot = vpot;
            this.leftBase = leftBase;
            this.leftSize = leftSize;
            this.nextBase = nextBase;
            this.nextSize = nextSize;
            this.lastBase = lastBase;
        }

        public void populate(Builder<VPotLED, Integer> builder) {
            for (VPotLEDPosition position: VPotLEDPosition.values()) {
                final int numeric;
                if (position.ordinal() < leftSize) {
                    numeric = leftBase + position.ordinal();
                } else if (position.ordinal() < leftSize + nextSize) {
                    numeric = nextBase - leftSize + position.ordinal();
                } else {
                    numeric = lastBase - (leftSize + nextSize) + position.ordinal();
                }

                builder.put(new VPotLED(vpot, position), numeric);
            }
        }

    }

}
