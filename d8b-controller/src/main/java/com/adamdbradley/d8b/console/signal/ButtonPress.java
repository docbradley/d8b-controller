package com.adamdbradley.d8b.console.signal;

import com.adamdbradley.d8b.console.ConsoleIdMaps;
import com.adamdbradley.d8b.console.PanelButton;

public class ButtonPress extends Signal {

    public final PanelButton button;

    public ButtonPress(final String command) {
        super(SignalType.ButtonPress);
        if (command.length() < 1 || command.length() > 3) {
            throw new IllegalArgumentException("Couldn't parse [" + command + "]");
        }
        this.button = ConsoleIdMaps.panelButtonLookup.get(Integer.parseInt(command, 16));
        if (button == null) {
            throw new IllegalArgumentException("Couldn't find [" + command + "]");
        }
    }

    @Override
    public String toString() {
        return timestamp + " ButonPress " + button;
    }

}
