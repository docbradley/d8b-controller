package com.adamdbradley.mcu.console.protocol.command;

import javax.sound.midi.InvalidMidiDataException;

import com.adamdbradley.mcu.console.protocol.ChannelControlChangeConsoleMessage;
import com.adamdbradley.mcu.console.protocol.Command;

public class WriteTimecode
extends ChannelControlChangeConsoleMessage
implements Command {

    /**
     * @param position 0 = LSD (TICKS3), 9 = MSD (BARS1),
     *                10 = ASSIGNMENT LSD, 11 = ASSIGNMENT MSD
     * @param character -- Yet another lookup table
     * @param dot -- Whether to set or clear the dot LED
     * @throws InvalidMidiDataException
     */
    public WriteTimecode(final byte position,
            final byte character,
            final boolean dot) {
        super((byte) 0x00,
                (byte) (0x40 | position),
                (byte) (character | (dot ? 0x40 : 0x00)));
        if ((character & 0xC0) != 0
                || position > 0x0B) {
            throw new IllegalArgumentException();
        }
    }

}
