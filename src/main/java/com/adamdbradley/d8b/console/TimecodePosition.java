package com.adamdbradley.d8b.console;

/**
 * The twelve eight-segment LED digit positions in the timecode display.
 */
public enum TimecodePosition {

    Block1Digit1, // Hours, Bars (MSD)
    Block1Digit2, 
    Block2Digit1, // Minutes, Bars (LSD)
    Block2Digit2,
    Block3Digit1, // Seconds, Beats
    Block3Digit2,
    Block4Digit1, // Frames, Ticks
    Block4Digit2,
    RangeFromDigit1,
    RangeFromDigit2,
    RangeToDigit1,
    RangeToDigit2

}
