package com.adamdbradley.d8b.console.signal;

import com.adamdbradley.d8b.console.VPot;

public class VPotMove extends Signal {

    public enum Direction {
        LEFT,
        RIGHT
    }

    public final VPot vpot;
    public final Direction direction;

    public VPotMove(final String command) {
        super(SignalType.VPotMove);
        if (command.length() != 4) {
            throw new IllegalArgumentException("Couldn't parse [" + command + "]");
        }
        this.vpot = VPot.values()[Integer.parseInt(command.substring(0, 2), 16)];
        final int value = Integer.parseInt(command.substring(2), 16);
        if ((value & 0x80) != 0) {
            direction = Direction.LEFT;
        } else {
            direction = Direction.RIGHT;
        }
    }

    @Override
    public String toString() {
        return timestamp + " VPot " + vpot + " " + direction;
    }

}
