package com.adamdbradley.d8b.console.signal;

import com.google.common.base.Charsets;

/**
 * Consumes bytes (read from the console's serial output)
 * and produces complete {@link Signal} objects as they are detected.
 */
public class ConsoleSignalParser {

    private String accumulator = "";
    private boolean isAlive = false;

    public synchronized void clearAlive() {
        isAlive = false;
    }
    public synchronized boolean isAlive() {
        return isAlive;
    }

    public synchronized Signal receiveCharacter(final byte nextCharacter) {
        if (nextCharacter == 'R' && accumulator.isEmpty()) {
            isAlive = true;
            return null;
        }

        accumulator += new String(new byte[] { nextCharacter }, Charsets.US_ASCII);

        final SignalType signalType = SignalType.typeOf(nextCharacter);
        if (signalType != null) {
            final Signal completeSignal = signalType.instantiate(accumulator);
            accumulator = "";
            return completeSignal;
        } else {
            return null;
        }
    }

}
