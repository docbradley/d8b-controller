package com.adamdbradley.d8b.console.command;

/**
 * A d8b serial command is a series of ASCII-7 upper-case
 * alphanumberic bytes terminated by a single lowercase
 * alphabetic byte which identifies a command parameterized
 * by the previous bytes.  This enumeration represents the
 * types of commands we know how to write and their lower-case
 * identifiers.
 */
enum CommandType {

    RequestSerialNumber('s'), // no args

    MoveFader('f'),
    LightPanelLED('i'),
    ShutPanelLED('j'),
    BlinkPanelLED('k'),

    SetChannelNumber('c'),

    ShutAllLEDs('a'),
    LightAllLEDs('b'),

    /**
     * Still trying to figure out exactly how this works; avoid if possible.
     */
    @Deprecated ClearScreen('z'),
    PositionCursor('u'),
    WriteScreenCharacter('v'),
    FlushScreen('x'),

    UpdateTimeScreen('t'),

    LightMeterLED('e'),
    ShutMeterLED('d'),

    Ping('o')
    ;

    public final byte commandIdentifier;

    CommandType(final char commandIdentifier) {
        this((byte) commandIdentifier);
    }

    CommandType(final byte commandIdentifier) {
        this.commandIdentifier = commandIdentifier;
    }

}
