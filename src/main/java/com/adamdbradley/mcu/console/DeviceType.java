package com.adamdbradley.mcu.console;

public enum DeviceType {

    Master((byte) 0x14),
    Extender((byte) 0x15),
    C4((byte) 0x17);

    private final byte encoding;
    private DeviceType(final byte encoding) {
        this.encoding = encoding;
    }

    public byte encode() {
        return encoding;
    }

    public static DeviceType decode(final byte deviceTypeByte) {
        for (DeviceType type: values()) {
            if (type.encode() == deviceTypeByte) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown device type " + Integer.toHexString(deviceTypeByte));
    }

}
