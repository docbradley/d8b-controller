package com.adamdbradley.mcu.console;

public enum Fader {

    Ch1,
    Ch2,
    Ch3,
    Ch4,
    Ch5,
    Ch6,
    Ch7,
    Ch8,
    Master;

    public Channel channel() {
        if (this == Master) {
            throw new IllegalStateException("Master fader doesn't have a Channel");
        } else {
            return Channel.values()[ordinal()];
        }
    }
}
