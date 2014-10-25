package com.adamdbradley.d8b.console.command;

import java.util.ArrayList;
import java.util.List;

import com.adamdbradley.d8b.console.VPot;
import com.adamdbradley.d8b.console.VPotLED;
import com.adamdbradley.d8b.console.VPotLEDPosition;

public abstract class VPotLEDHelper {

    private VPotLEDHelper() {}

    public static List<Command> clear(final VPot vpot, final boolean dot) {
        final List<Command> result = new ArrayList<>(12);

        for (VPotLEDPosition position: VPotLEDPosition.values()) {
            if (position == VPotLEDPosition.Dot && dot) {
                result.add(new LightVPotLED(new VPotLED(vpot, position)));
            } else {
                result.add(new ShutVPotLED(new VPotLED(vpot, position)));
            }
        }

        return result;
    }

    public static List<Command> singlePosition(final VPot vpot, final int value, final boolean dot) {
        if (value < -5 || value > 5) {
            throw new IllegalArgumentException();
        }

        final List<Command> result = new ArrayList<>(12);

        for (VPotLEDPosition position: VPotLEDPosition.values()) {
            if (position == VPotLEDPosition.Dot) {
                if (dot) {
                    result.add(new LightVPotLED(new VPotLED(vpot, VPotLEDPosition.Dot)));
                } else {
                    result.add(new ShutVPotLED(new VPotLED(vpot, VPotLEDPosition.Dot)));
                }
            } else {
                if (position.ordinal() == value + 5) {
                    result.add(new LightVPotLED(new VPotLED(vpot, position)));
                } else {
                    result.add(new ShutVPotLED(new VPotLED(vpot, position)));
                }
            }
        }

        return result;
    }

    public static List<Command> directionalFan(final VPot vpot, final int value, final boolean dot) {
        if (value < -5 || value > 5) {
            throw new IllegalArgumentException();
        }

        final List<Command> result = new ArrayList<>(12);

        for (final VPotLEDPosition position: VPotLEDPosition.values()) {
            if (position == VPotLEDPosition.Dot) {
                if (dot) {
                    result.add(new LightVPotLED(new VPotLED(vpot, VPotLEDPosition.Dot)));
                } else {
                    result.add(new ShutVPotLED(new VPotLED(vpot, VPotLEDPosition.Dot)));
                }
            } else {
                if ((value < 0) 
                        && (position.ordinal() < VPotLEDPosition.Center.ordinal())) {
                    // This is a "left" value and we're considering a left LED
                    if (position.ordinal() >= (value + 5)) {
                        result.add(new LightVPotLED(new VPotLED(vpot, position)));
                    } else {
                        result.add(new ShutVPotLED(new VPotLED(vpot, position)));
                    }
                } else if (position == VPotLEDPosition.Center) {
                    result.add(new LightVPotLED(new VPotLED(vpot, position)));
                } else if ((value > 0)
                        && (position.ordinal() > VPotLEDPosition.Center.ordinal())) {
                    // This is a "right" valu and we're considering a right LED
                    if (position.ordinal() <= (value + 5)) {
                        result.add(new LightVPotLED(new VPotLED(vpot, position)));
                    } else {
                        result.add(new ShutVPotLED(new VPotLED(vpot, position)));
                    }
                } else {
                    result.add(new ShutVPotLED(new VPotLED(vpot, position)));
                }
            }
        }

        return result;
    }

    public static List<Command> fanFromLeft(final VPot vpot, final int value, final boolean dot) {
        if (value < 0 || value > 10) {
            throw new IllegalArgumentException();
        }

        final List<Command> result = new ArrayList<>(12);

        for (VPotLEDPosition position: VPotLEDPosition.values()) {
            if (position == VPotLEDPosition.Dot) {
                if (dot) {
                    result.add(new LightVPotLED(new VPotLED(vpot, VPotLEDPosition.Dot)));
                } else {
                    result.add(new ShutVPotLED(new VPotLED(vpot, VPotLEDPosition.Dot)));
                }
            } else {
                if (position.ordinal() <= value) {
                    result.add(new LightVPotLED(new VPotLED(vpot, position)));
                } else {
                    result.add(new ShutVPotLED(new VPotLED(vpot, position)));
                }
            }
        }

        return result;
    }

    public static List<Command> fanFromCenter(final VPot vpot, final int value, final boolean dot) {
        if (value < 0 || value > 5) {
            throw new IllegalArgumentException();
        }

        final List<Command> result = new ArrayList<>(12);

        for (VPotLEDPosition position: VPotLEDPosition.values()) {
            if (position == VPotLEDPosition.Dot) {
                if (dot) {
                    result.add(new LightVPotLED(new VPotLED(vpot, VPotLEDPosition.Dot)));
                } else {
                    result.add(new ShutVPotLED(new VPotLED(vpot, VPotLEDPosition.Dot)));
                }
            } else {
                if (Math.abs(position.ordinal() - 5) >= value) {
                    result.add(new LightVPotLED(new VPotLED(vpot, position)));
                } else {
                    result.add(new ShutVPotLED(new VPotLED(vpot, position)));
                }
            }
        }

        return result;
    }

}
