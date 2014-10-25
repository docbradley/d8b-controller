package com.adamdbradley.mcu.console.protocol.signal;

import com.adamdbradley.mcu.console.protocol.ChannelControlChangeConsoleMessage;
import com.adamdbradley.mcu.console.protocol.Signal;

public class JogRight
extends ChannelControlChangeConsoleMessage
implements Signal {

    public JogRight() {
        super((byte) 0x0,
                (byte) 0x3C,
                (byte) 0x01);
    }

}
