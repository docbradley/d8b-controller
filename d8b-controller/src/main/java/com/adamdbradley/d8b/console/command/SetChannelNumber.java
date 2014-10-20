package com.adamdbradley.d8b.console.command;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Controls the two-digit "channel number" indicator.
 */
public class SetChannelNumber extends Command {

    public static final Set<Character> ALLOWED_CHARACTERS = ImmutableSet.of(
            ' ',
            'F',
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9'
            );

    private final String value;

    public SetChannelNumber(final Integer channelNumber) {
        super(CommandType.SetChannelNumber);
        if (channelNumber != null) {
            this.value = Integer.toString(channelNumber);
        } else {
            this.value = "FF";
        }
    }

    /**
     * "FF" or similar to blank it
     * @param value
     */
    public SetChannelNumber(final String value) {
        super(CommandType.SetChannelNumber);
        if (value.length() <1 || value.length() > 2) {
            throw new IllegalArgumentException("Value must be of length 1 or 2: [" + value + "]");
        }

        if (!ALLOWED_CHARACTERS.contains(value.charAt(0))) {
            throw new IllegalArgumentException("Illegal character [" + value.charAt(0) + "]");
        }
        if (value.length() > 1 && !ALLOWED_CHARACTERS.contains(value.charAt(1))) {
            throw new IllegalArgumentException("Illegal character [" + value.charAt(1) + "]");
        }

        this.value = value;
    }

    @Override
    public String serializeParameters() {
        return value;
    }

}
