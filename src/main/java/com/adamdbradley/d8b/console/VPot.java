package com.adamdbradley.d8b.console;

/**
 * The physical VPots on the console.
 */
public enum VPot {

    Ch1,
    Ch2,
    Ch3,
    Ch4,
    Ch5,
    Ch6,
    Ch7,
    Ch8,
    Ch9,
    Ch10,
    Ch11,
    Ch12,
    Ch13,
    Ch14,
    Ch15,
    Ch16,
    Ch17,
    Ch18,
    Ch19,
    Ch20,
    Ch21,
    Ch22,
    Ch23,
    Ch24,
    Master,
    Low,
    LowMid,
    HiMid,
    Hi,
    SoloStudioLevel,
    PhonesCueMix1Level,
    PhonesCueMix2Level,
    SpeakerLevel;

    public boolean hasFader() {
        return name().matches("^Ch[0-9]+");
    }
    public Fader getFader() {
        if (hasFader()) {
            return Fader.values()[ordinal()];
        } else {
            throw new IllegalStateException("VPot " + name() + " doesn't have a Fader");
        }
    }

}
