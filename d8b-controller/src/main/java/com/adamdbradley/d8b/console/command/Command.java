package com.adamdbradley.d8b.console.command;

/**
 * Instances represent surface signals sent from the d8b console.
 * Command objects are immutable.
 */
public abstract class Command {

    public final CommandType type;

    protected Command(final CommandType type) {
        this.type = type;
    }

    public final String serialize() {
        return serializeParameters().toUpperCase() + (char) type.commandIdentifier;
    }

    public abstract String serializeParameters();


    /*
     * Helper functions
     */

    protected String toTwoDigitHexString(final int value) {
        if (value < 0 || value > 0x000000ff) {
            throw new IllegalArgumentException("Can't fit " + value);
        }
        final String asHex = Integer.toHexString(value);
        if (asHex.length() == 1) {
            return "0" + asHex;
        } else {
            return asHex;
        }
    }

    protected String toThreeDigitHexString(final int value) {
        if (value < 0 || value > 0x00000fff) {
            throw new IllegalArgumentException("Can't fit " + value);
        }
        final String asHex = Integer.toHexString(value);
        switch (asHex.length()) {
        case 1:
            return "00" + asHex;
        case 2:
            return "0" + asHex;
        case 3:
            return asHex;
        default:
            throw new IllegalStateException(asHex);
        }
    }

}
