package com.adamdbradley.d8b.console.signal;

import java.math.BigInteger;

import com.google.common.base.Strings;

/**
 * The self-reported serial number of the console.
 */
public class SerialNumber extends Signal {

    public final BigInteger serialNumber;

    public SerialNumber(final String command) {
        super(SignalType.SerialNumber);
        this.serialNumber = new BigInteger(command, 16);
    }

    @Override
    public String toString() {
        return timestamp + " SerialNumber "
                + Strings.padStart(serialNumber.toString(16), 12, '0');
    }

}
