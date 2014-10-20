package com.adamdbradley.mcu.console.protocol.signal;

import com.adamdbradley.mcu.console.PanelButton;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ButtonReleased
extends ButtonMessage
implements Signal {

    public ButtonReleased(PanelButton button) {
        super(button, false);
    }

}
