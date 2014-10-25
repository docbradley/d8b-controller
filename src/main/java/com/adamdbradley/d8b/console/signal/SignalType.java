package com.adamdbradley.d8b.console.signal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Helper class for recognizing and parsing Console
 * {@link Signal} messages.
 * <p/>
 * {@link Signal} messages consist of strings of uppercase alphanumeric
 * (usually hexidecimal) bytes followed by a lower-case ASCII
 * character (or other recognizable character; e.g.,  {@link JogLeft}
 * and {@link JogRight}).
 */
public enum SignalType {

    FaderMove('f', FaderMove.class),
    ButtonPress('s', ButtonPress.class),
    ButtonRelease('u', ButtonRelease.class),
    VPotMove('v', VPotMove.class),
    JogLeft('-', JogLeft.class),
    JogRight('+', JogRight.class),
    Heartbeat1('k', Heartbeat1.class),
    Heartbeat2('l', Heartbeat2.class),
    Pong('o', Pong.class),

    SerialNumber('c', SerialNumber.class),
    UnknownP('p', UnknownP.class)
    ;

    private final byte commandIdentifier;
    private final Constructor<? extends Signal> constructor;

    SignalType(char commandIdentifier, Class<? extends Signal> signalClass) {
        this((byte) commandIdentifier, signalClass);
    }

    SignalType(byte commandIdentifier, Class<? extends Signal> signalClass) {
        this.commandIdentifier = commandIdentifier;
        try {
            this.constructor = signalClass.getConstructor(String.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public static SignalType typeOf(final byte character) {
        for (SignalType signalType: values()) {
            if (signalType.commandIdentifier == character) {
                return signalType;
            }
        }
        return null;
    }

    public Signal instantiate(final String command) {
        try {
            if (command.length() == 1) {
                return constructor.newInstance("");
            } else {
                return constructor.newInstance(command.substring(0, command.length() - 1));
            }
        } catch (InvocationTargetException e) {
            System.err.println("Couldn't instantiate " + constructor.getName() + "::" + command
                    + " -- " + e.getCause().getClass().getSimpleName()
                    + "::" + e.getCause().getMessage());
            return null;
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
    }

}
