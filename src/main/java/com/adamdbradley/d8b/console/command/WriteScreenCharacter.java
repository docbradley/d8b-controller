package com.adamdbradley.d8b.console.command;

/**
 * Write a single character to the screen at the current cursor
 * position and move the cursor position forward.
 */
public class WriteScreenCharacter extends Command {

    private final byte character;

    public WriteScreenCharacter(final char character) {
        this((byte) character);
    }

    protected WriteScreenCharacter(final byte character) {
        super(CommandType.WriteScreenCharacter);
        if (character > 127 || character < 20) {
            throw new IllegalArgumentException("Can't represent as ASCII: "
                    + Integer.toHexString(character));
        }
        this.character = character;
    }

    @Override
    public String serializeParameters() {
        return toTwoDigitHexString(character);
    }

}
