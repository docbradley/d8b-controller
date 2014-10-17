package com.adamdbradley.mcu.console.protocol.command;

import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.protocol.Command;
import com.adamdbradley.mcu.console.protocol.MCUSysexMessage;
import com.google.common.collect.ImmutableMap;

public class WriteScreen
implements Command {

    public final DeviceType deviceType;

    private final MCUSysexMessage message;

    public WriteScreen(final DeviceType deviceType,
            final int row, final int column,
            final String string) {
        try {
            message = new MCUSysexMessage(deviceType,
                    build(row, column, string));
        } catch (InvalidMidiDataException e) {
            throw new IllegalStateException(e);
        }
        this.deviceType = deviceType;
    }

    @Override
    public MidiMessage getMessage() {
        return message;
    }

    private static byte[] build(int row, int column, String string) {
        final int startPosition = (row * 0x38) + column;
        
        final byte[] payload = new byte[string.length() + 2];
        payload[0] = 0x12;
        payload[1] = (byte) startPosition;
        for (int i=0; i<string.length(); i++) {
            final char character = string.charAt(i);
            payload[2 + i] = map(character);
        }
        return payload;
    }

    private static byte map(final char character) {
        if (charEncoding.containsKey(character)) {
            return charEncoding.get(character);
        } else {
            return (byte) 0x3f;
        }
    }

    // See http://stash.reaper.fm/12333/HUI_CSET.txt
    private static final Map<Character, Byte> charEncoding = ImmutableMap.<Character, Byte>builder()
            // 00 through 1F are extended (Latin-1-ish?) characters
            .put(' ', (byte) 0x20)
            .put('!', (byte) 0x21)
            .put('"', (byte) 0x22)
            .put('#', (byte) 0x23)
            .put('$', (byte) 0x24)
            .put('%', (byte) 0x25)
            .put('&', (byte) 0x26)
            .put('\'', (byte) 0x27)
            .put('(', (byte) 0x28)
            .put(')', (byte) 0x29)
            .put('*', (byte) 0x2A)
            .put('+', (byte) 0x2B)
            .put(',', (byte) 0x2C)
            .put('-', (byte) 0x2D)
            .put('.', (byte) 0x2E)
            .put('/', (byte) 0x2F)
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
            .put(':', (byte) 0x3A)
            .put(';', (byte) 0x3B)
            .put('<', (byte) 0x3C)
            .put('=', (byte) 0x3D)
            .put('>', (byte) 0x3E)
            .put('?', (byte) 0x3F)
            .put('@', (byte) 0x40)
            .put('A', (byte) 0x41)
            .put('B', (byte) 0x42)
            .put('C', (byte) 0x43)
            .put('D', (byte) 0x44)
            .put('E', (byte) 0x45)
            .put('F', (byte) 0x46)
            .put('G', (byte) 0x47)
            .put('H', (byte) 0x48)
            .put('I', (byte) 0x49)
            .put('J', (byte) 0x4A)
            .put('K', (byte) 0x4B)
            .put('L', (byte) 0x4C)
            .put('M', (byte) 0x4D)
            .put('N', (byte) 0x4E)
            .put('O', (byte) 0x4F)
            .put('P', (byte) 0x50)
            .put('Q', (byte) 0x51)
            .put('R', (byte) 0x52)
            .put('S', (byte) 0x53)
            .put('T', (byte) 0x54)
            .put('U', (byte) 0x55)
            .put('V', (byte) 0x56)
            .put('W', (byte) 0x57)
            .put('X', (byte) 0x58)
            .put('Y', (byte) 0x59)
            .put('Z', (byte) 0x5A)
            .put('[', (byte) 0x5B)
            .put('\\', (byte) 0x5C)
            .put(']', (byte) 0x5D)
            .put('^', (byte) 0x5E)
            .put('_', (byte) 0x5F)
            .put('`', (byte) 0x60)
            .put('a', (byte) 0x61)
            .put('b', (byte) 0x62)
            .put('c', (byte) 0x63)
            .put('d', (byte) 0x64)
            .put('e', (byte) 0x65)
            .put('f', (byte) 0x66)
            .put('g', (byte) 0x67)
            .put('h', (byte) 0x68)
            .put('i', (byte) 0x69)
            .put('j', (byte) 0x6A)
            .put('k', (byte) 0x6B)
            .put('l', (byte) 0x6C)
            .put('m', (byte) 0x6D)
            .put('n', (byte) 0x6E)
            .put('o', (byte) 0x6F)
            .put('p', (byte) 0x70)
            .put('q', (byte) 0x71)
            .put('r', (byte) 0x72)
            .put('s', (byte) 0x73)
            .put('t', (byte) 0x74)
            .put('u', (byte) 0x75)
            .put('v', (byte) 0x76)
            .put('w', (byte) 0x77)
            .put('x', (byte) 0x78)
            .put('y', (byte) 0x79)
            .put('z', (byte) 0x7A)
            .put('{', (byte) 0x7B)
            .put('|', (byte) 0x7C)
            .put('}', (byte) 0x7D)
            .put('~', (byte) 0x7E)
            // .put(' ', (byte) 0x7F) // fuzzy block
            .build();

}
