package com.adamdbradley.d8b.console;

/**
 * An individual LED for an individual VPot.
 */
public class VPotLED {

    public final VPot vpot;
    public final VPotLEDPosition position;

    public VPotLED(final VPot vpot, final VPotLEDPosition position) {
        this.vpot = vpot;
        this.position = position;
    }


    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return vpot + ":" + position;
    }

    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        } else {
            return this.toString().equals(that.toString());
        }
    }

}
