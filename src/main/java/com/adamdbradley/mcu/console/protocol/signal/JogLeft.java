package com.adamdbradley.mcu.console.protocol.signal;

import com.adamdbradley.mcu.console.protocol.ChannelControlChangeConsoleMessage;
import com.adamdbradley.mcu.console.protocol.Signal;

public class JogLeft
extends ChannelControlChangeConsoleMessage
implements Signal {

    public JogLeft() {
        super((byte) 0x0,
                (byte) 0x3C,
                (byte) 0x41);
    }

}
