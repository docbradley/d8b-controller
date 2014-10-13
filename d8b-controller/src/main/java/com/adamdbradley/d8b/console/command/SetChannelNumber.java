package com.adamdbradley.d8b.console.command;

/**
 * Controls the two-digit "channel number" indicator.
 */
public class SetChannelNumber extends Command {

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
            throw new RuntimeException("Value must be of length 1 or 2: [" + value + "]");
        }
        this.value = value;
    }

    @Override
    public String serializeParameters() {
        return value;
    }

}
