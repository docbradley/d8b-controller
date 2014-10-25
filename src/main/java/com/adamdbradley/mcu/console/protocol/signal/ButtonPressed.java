package com.adamdbradley.mcu.console.protocol.signal;

import com.adamdbradley.mcu.console.PanelButton;
import com.adamdbradley.mcu.console.protocol.Signal;

public class ButtonPressed
extends ButtonMessage
implements Signal {

    public ButtonPressed(PanelButton button) {
        super(button, true);
    }

}
