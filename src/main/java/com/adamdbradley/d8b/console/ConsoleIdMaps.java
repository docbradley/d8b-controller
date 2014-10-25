package com.adamdbradley.d8b.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

/**
 * Maintains mappings between the IDs used in the console serial
 * protocol to identify buttons, faders, vpots, and LEDs and our
 * domain enumerations of them.  Note that we use actual integers
 * in these lookup tables, but in the serial protocol theu are
 * always represented as ASCII strings of hexidecimal digits.
 */
public class ConsoleIdMaps {

    public static final BiMap<Integer, PanelButton> panelButtonLookup;
    public static final BiMap<Integer, Fader> faderLookup;
    public static final BiMap<Integer, VPot> vpotLookup;

    public static final BiMap<PanelLED, Integer> panelLEDLookup;

    static {
        try {
            final ImmutableBiMap.Builder<Integer, PanelButton> panelButtonLookupBuilder = ImmutableBiMap.builder();
            try (FileReader fr = new FileReader(new File("./firmware/switch.txt"))) {
                try (BufferedReader br = new BufferedReader(fr)) {
                    try (Stream<String> stream = br.lines()) {
                        stream.forEach(new Consumer<String>() {
                            @Override
                            public void accept(final String t) {
                                if (t.trim().isEmpty()) {
                                    return;
                                }
                                final String[] split = t.trim().replaceAll("\\s", " ").split(" ");
                                final Integer numericId = Integer.parseInt(split[0], 16);
                                final String name = normalizeName(Arrays.copyOfRange(split, 1, split.length));
                                final PanelButton panelButton = findByNormalizedName(PanelButton.class, name);
                                panelButtonLookupBuilder.put(numericId, panelButton);
                            }
                        });
                    }
                }
            }
            panelButtonLookup = panelButtonLookupBuilder.build();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException("Couldn't set up Button map", e);
        }


        try {
            final ImmutableBiMap.Builder<PanelLED, Integer> panelLEDLookupBuilder = ImmutableBiMap.builder();
            try (FileReader fr = new FileReader(new File("./firmware/led.txt"))) {
                try (BufferedReader br = new BufferedReader(fr)) {
                    try (Stream<String> stream = br.lines()) {
                        stream.forEach(new Consumer<String>() {
                            @Override
                            public void accept(final String t) {
                                if (t.trim().isEmpty()) {
                                    return;
                                }
                                final String[] split = t.trim().replaceAll("\\s", " ").split(" ");
                                final Integer numericId = Integer.parseInt(split[0], 16);
                                final String name = normalizeName(Arrays.copyOfRange(split, 1, split.length));
                                final PanelLED panelLED = findByNormalizedName(PanelLED.class, name);
                                panelLEDLookupBuilder.put(panelLED, numericId);
                            }
                        });
                    }
                }
            }
            panelLEDLookup = panelLEDLookupBuilder.build();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException("Couldn't set up LED maps", e);
        }


        final ImmutableBiMap.Builder<Integer, Fader> faderLookupBuilder = ImmutableBiMap.builder();
        final String faderNamePrefix = Fader.Ch1.name().split("[0-9]")[0];
        for (int i = 0; i < 0x18; i++) {
            faderLookupBuilder.put(Integer.valueOf(i),
                    Fader.valueOf(faderNamePrefix + (i+1)));
        }
        faderLookupBuilder.put(Integer.valueOf(0x18), Fader.Master);
        faderLookup = faderLookupBuilder.build();


        final ImmutableBiMap.Builder<Integer, VPot> vpotLookupBuilder = ImmutableBiMap.builder();
        final String vpotChannelNamePrefix = VPot.Ch1.name().split("[0-9]")[0];
        for (int i=0; i<24; i++) {
            vpotLookupBuilder.put(Integer.valueOf(i),
                    VPot.valueOf(vpotChannelNamePrefix + (i+1)));
        }
        vpotLookup = vpotLookupBuilder.build();
    }








    public interface Aliased {
        public String[] aliases();
    }

    private static <T extends Enum<T> & Aliased> T findByNormalizedName(
            final Class<T> clazz, final String normalizedName) {
        for (final T enumValue: clazz.getEnumConstants()) {
            for (String alias: ((Aliased)enumValue).aliases()) {
                if (normalize(alias).equals(normalizedName)) {
                    return enumValue;
                }
            }
        }
        throw new IllegalArgumentException("Couldn't find " + normalizedName + " in " + clazz);
    }

    private static String normalizeName(final String[] split) {
        if (Character.isDigit(split[0].charAt(0))) {
            split[0] = "ch" + split[0];
        }
        return normalize(split);
    }

    private static String normalize(String ... strings) {
        StringBuilder sb = new StringBuilder();
        for (final String string: strings) {
            if (string != null) {
                sb.append(string);
            }
        }
        return sb.toString().replaceAll("[^a-zA-Z0-9]", "");
    }





    public static final Map<MeterLEDChannel, MeterLEDNumberChannelLookup> meterLEDChannelLookup = ImmutableMap
            .<MeterLEDChannel, MeterLEDNumberChannelLookup>builder()
            .put(MeterLEDChannel.Ch1, new MeterLEDNumberChannelLookup(0x10C, 0x0F0))
            .put(MeterLEDChannel.Ch2, new MeterLEDNumberChannelLookup(0x100))
            .put(MeterLEDChannel.Ch3, new MeterLEDNumberChannelLookup(0x114))
            .put(MeterLEDChannel.Ch4, new MeterLEDNumberChannelLookup(0x128, 0x12C, 0x110))
            .put(MeterLEDChannel.Ch5, new MeterLEDNumberChannelLookup(0x13C, 0x120))
            .put(MeterLEDChannel.Ch6, new MeterLEDNumberChannelLookup(0x130))
            .put(MeterLEDChannel.Ch7, new MeterLEDNumberChannelLookup(0x144))
            .put(MeterLEDChannel.Ch8, new MeterLEDNumberChannelLookup(0x158, 0x15C, 0x140))
            .put(MeterLEDChannel.Ch9, new MeterLEDNumberChannelLookup(0x16C, 0x150))
            .put(MeterLEDChannel.Ch10, new MeterLEDNumberChannelLookup(0x160))
            .put(MeterLEDChannel.Ch11, new MeterLEDNumberChannelLookup(0x174))
            .put(MeterLEDChannel.Ch12, new MeterLEDNumberChannelLookup(0x188, 0x18c, 0x170))
            .put(MeterLEDChannel.Ch13, new MeterLEDNumberChannelLookup(0x19C, 0x180))
            .put(MeterLEDChannel.Ch14, new MeterLEDNumberChannelLookup(0x190))
            .put(MeterLEDChannel.Ch15, new MeterLEDNumberChannelLookup(0x1A4))
            .put(MeterLEDChannel.Ch16, new MeterLEDNumberChannelLookup(0x1B8, 0x1BC, 0x1A0))
            .put(MeterLEDChannel.Ch17, new MeterLEDNumberChannelLookup(0x1CC, 0x1B0))
            .put(MeterLEDChannel.Ch18, new MeterLEDNumberChannelLookup(0x1C0))
            .put(MeterLEDChannel.Ch19, new MeterLEDNumberChannelLookup(0x1D4))
            .put(MeterLEDChannel.Ch20, new MeterLEDNumberChannelLookup(0x1E8, 0x1EC, 0x1D0))
            .put(MeterLEDChannel.Ch21, new MeterLEDNumberChannelLookup(0x1FC, 0x1E0))
            .put(MeterLEDChannel.Ch22, new MeterLEDNumberChannelLookup(0x1F0))
            .put(MeterLEDChannel.Ch23, new MeterLEDNumberChannelLookup(0x204))
            .put(MeterLEDChannel.Ch24, new MeterLEDNumberChannelLookup(0x218, 0x21C, 0x200))
            .put(MeterLEDChannel.MasterLeft, new MeterLEDNumberChannelLookup(0x22C, 0x210))
            .put(MeterLEDChannel.MasterRight, new MeterLEDNumberChannelLookup(0x220))
            .build();

    public static class MeterLEDNumberChannelLookup {
        // The numeric identifier of the red LED.
        // The three yellos below are always numbered N+1, N+2, N+3.
        private final int topRangeBase;

        // The numeric identifier of the highest green LED.
        // The three below are always numbered N+1, N+2, N+3.
        private final int upperGreenRangeBase;

        // The numeric identifier of the fourth (counting from bottom) green LED.
        // Often, but not always, #upperGreenRangeBase + 4.
        // The three below are always numbered N+1, N+2, N+3.
        private final int lowerGreenRangeBase;

        private MeterLEDNumberChannelLookup(final int topRangeBase) {
            this(topRangeBase, topRangeBase + 4);
        }

        private MeterLEDNumberChannelLookup(final int topRangeBase,
                final int upperGreenRangeBase) {
            this(topRangeBase, upperGreenRangeBase, upperGreenRangeBase + 4);
        }

        private MeterLEDNumberChannelLookup(final int topRangeBase,
                final int upperGreenRangeBase, final int lowerGreenRangeBase) {
            this.topRangeBase = topRangeBase;
            this.upperGreenRangeBase = upperGreenRangeBase;
            this.lowerGreenRangeBase = lowerGreenRangeBase;
        }

        public int getLEDId(final MeterLEDNumber number) {
            final int ordinalFromBottom = number.ordinal() + 1 - MeterLEDNumber.Level1.ordinal();
            if (ordinalFromBottom < 4) {
                return lowerGreenRangeBase + 3 - ordinalFromBottom;
            } else if (ordinalFromBottom < 8) {
                return upperGreenRangeBase + 7 - ordinalFromBottom;
            } else {
                return topRangeBase + 11 - ordinalFromBottom;
            }
        }
    }

}
