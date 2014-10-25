package com.adamdbradley.d8b.console.command;

import com.adamdbradley.d8b.console.TimecodePosition;

/**
 * Update a single 8-segment digit of the timecode display.
 */
public class UpdateTimecode extends Command {

    private TimecodePosition position;
    private Integer value;
    private boolean dot;

    /**
     * 
     * @param position (0 = MSD, 11 = LSD)
     * @param value 0-9, or null (empty), or -1 (pause).
     * @param dot
     */
    public UpdateTimecode(final TimecodePosition position,
            final Integer value, final boolean dot) {
        super(CommandType.UpdateTimeScreen);

        this.position = position;

        if (value != null && (value < -1 || value > 9)) {
            throw new IllegalArgumentException("Unknown value " + value);
        }
        this.value = value;

        this.dot = dot;
    }

    @Override
    public String serializeParameters() {
        return toTwoDigitHexString(position.ordinal())
                + (dot ? "1" : "0")
                + (value == null ? "A" : (value == -1 ? "B" : value.toString()));
    }

}
