package com.adamdbradley.mcu.console;

public enum Channel {

    Ch1,
    Ch2,
    Ch3,
    Ch4,
    Ch5,
    Ch6,
    Ch7,
    Ch8;

    public Fader fader() {
        return Fader.values()[ordinal()];
    }

}
