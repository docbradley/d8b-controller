package com.adamdbradley.mcu.console.protocol.command;

import java.util.Set;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.protocol.ChannelControlChangeConsoleMessage;
import com.adamdbradley.mcu.console.protocol.Command;
import com.google.common.collect.ImmutableBiMap;

public class WriteTimecode
extends ChannelControlChangeConsoleMessage
implements Command {

    public final byte position;
    public final char character;
    public final boolean dot;

    /**
     * @param position 0 = LSD (TICKS3), 9 = MSD (BARS1),
     *                10 = ASSIGNMENT LSD, 11 = ASSIGNMENT MSD
     * @param character -- Yet another lookup table
     * @param dot -- Whether to set or clear the dot LED
     * @throws InvalidMidiDataException
     */
    public WriteTimecode(final byte position,
            final char character,
            final boolean dot) {
        super((byte) 0x00,
                (byte) (0x40 | position),
                (byte) (encode(character) | (dot ? 0x40 : 0x00)));
        if (position > 0x0B) {
            throw new IllegalArgumentException("Illegal position");
        }

        this.position = position;
        this.character = character;
        this.dot = dot;
    }


    public static byte encode(final char character) {
        if (encode.containsKey(character)) {
            return encode.get(character);
        } else {
            throw new IllegalArgumentException("Unrecognized character " + character);
        }
    }

    public static char decide(final byte encodedCharacter) {
        if (encode.inverse().containsKey(encodedCharacter)) {
            return encode.inverse().get(encodedCharacter);
        } else {
            throw new IllegalArgumentException("Unknown encoded char " + Integer.toHexString(encodedCharacter));
        }
    }

    private static final ImmutableBiMap<Character, Byte> encode = ImmutableBiMap
            .<Character, Byte>builder()
            .put(' ', (byte) 0x0)
            .put('A', (byte) 0x1)
            .put('b', (byte) 0x2)
            .put('C', (byte) 0x3)
            .put('d', (byte) 0x4)
            .put('E', (byte) 0x5)
            .put('F', (byte) 0x6)
            .put('G', (byte) 0x7)
            .put('h', (byte) 0x8)
            .put('I', (byte) 0x9)
            .put('J', (byte) 0xA)
            .put('K', (byte) 0xB)
            .put('L', (byte) 0xC)
            .put('N', (byte) 0xD)
            .put('n', (byte) 0xE)
            .put('o', (byte) 0xF)
            .put('P', (byte) 0x10)
            .put('q', (byte) 0x11)
            .put('r', (byte) 0x12)
            .put('S', (byte) 0x13)
            .put('t', (byte) 0x14)
            .put('u', (byte) 0x15)
            // gap
            .put('U', (byte) 0x17)
            .put('H', (byte) 0x18)
            .put('y', (byte) 0x19)
            .put('Z', (byte) 0x20)
            // gap
            .put('\u00B0', (byte) 0x2A) // degree
            // gap
            .put('-', (byte) 0x2D)
            .put('_', (byte) 0x2E)
            // gap
            .put('0', (byte) 0x30)
            .put('1', (byte) 0x31)
            .put('2', (byte) 0x32)
            .put('3', (byte) 0x33)
            .put('4', (byte) 0x34)
            .put('5', (byte) 0x35)
            .put('6', (byte) 0x36)
            .put('7', (byte) 0x37)
            .put('8', (byte) 0x38)
            .put('9', (byte) 0x39)
            .build();

    public static Set<Character> supportedChars() {
        return encode.keySet();
    }

}
