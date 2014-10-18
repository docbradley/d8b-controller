package com.adamdbradley.d8b;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.adamdbradley.d8b.console.Fader;
import com.adamdbradley.d8b.console.PanelButton;
import com.adamdbradley.d8b.console.signal.ButtonPress;
import com.adamdbradley.d8b.console.signal.ButtonRelease;
import com.adamdbradley.d8b.console.signal.FaderMove;
import com.adamdbradley.d8b.console.signal.Signal;
import com.adamdbradley.d8b.console.signal.SignalType;

/**
 * Remember the most recent events we've seen for buttons and faders,
 * so we can tell whether an event is an actual change of status or not.
 * For now, mostly useful for squelching logging and console output.
 */
public class ConsoleStatusMap {

    private final Map<PanelButton, Boolean> buttons = new ConcurrentHashMap<>();
    private final Map<Fader, Integer> faders = new ConcurrentHashMap<>();

    public boolean isUpdate(final Signal signal) {
        switch (signal.type) {
        case ButtonPress:
        case ButtonRelease:
            return handleButtonEvent(signal);

        case FaderMove:
            final Fader fader = ((FaderMove) signal).fader;
            final Integer faderValue = Integer.valueOf(((FaderMove) signal).position);
            if (faderValue.equals(faders.get(fader))) {
                return false;
            }
            faders.put(fader, faderValue);
            return true;

        default:
            // No trackable state, so it's an "update"
            return true;
        }
    }

    private boolean handleButtonEvent(Signal signal) {
        final PanelButton button;
        if (signal instanceof ButtonPress) {
            button = ((ButtonPress) signal).button;
        } else {
            button = ((ButtonRelease) signal).button;
        }
        final Boolean newButtonState = Boolean.valueOf(signal.type == SignalType.ButtonPress);
        if (newButtonState.equals(buttons.get(button))) {
            return false;
        }
        buttons.put(button, newButtonState);
        return true;
    }

}
