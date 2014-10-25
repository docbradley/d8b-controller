package com.adamdbradley.d8b.console.command;

/**
 * Useful before {@link WriteScreenCharacter}.
 */
public class PositionCursor extends Command {

    private final int row;
    private final int column;

    /**
     * 
     * @param row (basis-0)
     * @param column (basis-0)
     */
    public PositionCursor(final int row, final int column) {
        super(CommandType.PositionCursor);
        this.row = row;
        this.column = column;
    }

    @Override
    public String serializeParameters() {
        final int value;
        switch (row) {
        case 0:
            value = 0x80 + column;
            break;
        case 1:
            value = 0xC0 + column;
            break;
        default:
            throw new IllegalStateException("Don't know about " + row);
        }
        return toTwoDigitHexString(value);
    }

}
