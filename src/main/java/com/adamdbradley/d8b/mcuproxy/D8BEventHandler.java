package com.adamdbradley.d8b.mcuproxy;

import com.adamdbradley.d8b.console.Fader;
import com.adamdbradley.d8b.console.signal.ButtonPress;
import com.adamdbradley.d8b.console.signal.ButtonRelease;
import com.adamdbradley.d8b.console.signal.FaderMove;
import com.adamdbradley.d8b.console.signal.Heartbeat1;
import com.adamdbradley.d8b.console.signal.Heartbeat2;
import com.adamdbradley.d8b.console.signal.JogLeft;
import com.adamdbradley.d8b.console.signal.JogRight;
import com.adamdbradley.d8b.console.signal.Pong;
import com.adamdbradley.d8b.console.signal.SerialNumber;
import com.adamdbradley.d8b.console.signal.Signal;
import com.adamdbradley.d8b.console.signal.VPotMove;
import com.adamdbradley.d8b.console.signal.VPotMove.Direction;
import com.adamdbradley.mcu.console.ChannelButton;
import com.adamdbradley.mcu.console.protocol.signal.ChannelButtonPressed;
// Import d8b symbols, reference MCU symbols with fully-qualified names
import com.adamdbradley.mcu.console.protocol.signal.ChannelButtonReleased;

// Class must be public so Application.typedDispatch can find methods
public class D8BEventHandler {

    private final Proxy proxy;

    D8BEventHandler(final Proxy proxy) {
        this.proxy = proxy;
    }

    public void dispatch(final ButtonPress signal) {
        final com.adamdbradley.mcu.console.PanelButton button = ControllerMaps.mapToPanel(signal.button);
        if (button != null) {
            proxy.sendMasterEmulatorSignal(new com.adamdbradley.mcu.console.protocol.signal.ButtonPressed(button));
            return;
        }

        if (signal.button.isChannelButton()) {
            final Fader channel = signal.button.getChannel();

            final int mcuBank = 2 - (channel.ordinal() / 8);
            final com.adamdbradley.mcu.console.Channel mcuChannel = com.adamdbradley.mcu.console.Channel.values()[channel.ordinal() % 8];
            if (signal.button.name().endsWith("_RecReadyProxy")) {
                proxy.sendEmulatorSignal(mcuBank, new ChannelButtonPressed(mcuChannel, ChannelButton.REC));
            } else if (signal.button.name().endsWith("_Select")) {
                proxy.sendEmulatorSignal(mcuBank, new ChannelButtonPressed(mcuChannel, ChannelButton.SELECT));
            } else if (signal.button.name().endsWith("_Solo")) {
                proxy.sendEmulatorSignal(mcuBank, new ChannelButtonPressed(mcuChannel, ChannelButton.SOLO));
            } else if (signal.button.name().endsWith("_Mute")) {
                proxy.sendEmulatorSignal(mcuBank, new ChannelButtonPressed(mcuChannel, ChannelButton.MUTE));
            // } else if (signal.button.name().endsWith("_Assign")) {
            // } else if (signal.button.name().endsWith("_Write")) {
            } else {
                System.err.println("Don't know what to do with " + signal);
            }
            return;
        }

        switch (signal.button) {
        case vfdmanager_menuleft:
            proxy.shiftVirtualScreenRight();
            return;
        case vfdmanager_menuright:
            proxy.shiftVirtualScreenLeft();
            return;
        default:
            System.err.println("Ignoring unmapped " + signal);
        }
    }

    public void dispatch(final ButtonRelease signal) {
        final com.adamdbradley.mcu.console.PanelButton button = ControllerMaps.mapToPanel(signal.button);
        if (button != null) {
            proxy.sendMasterEmulatorSignal(new com.adamdbradley.mcu.console.protocol.signal.ButtonReleased(button));
            return;
        }

        if (signal.button.isChannelButton()) {
            final Fader channel = signal.button.getChannel();

            final int mcuBank = 2 - (channel.ordinal() / 8);
            final com.adamdbradley.mcu.console.Channel mcuChannel = com.adamdbradley.mcu.console.Channel.values()[channel.ordinal() % 8];
            if (signal.button.name().endsWith("_RecReadyProxy")) {
                proxy.sendEmulatorSignal(mcuBank, new ChannelButtonReleased(mcuChannel, ChannelButton.REC));
            } else if (signal.button.name().endsWith("_Select")) {
                proxy.sendEmulatorSignal(mcuBank, new ChannelButtonReleased(mcuChannel, ChannelButton.SELECT));
            } else if (signal.button.name().endsWith("_Solo")) {
                proxy.sendEmulatorSignal(mcuBank, new ChannelButtonReleased(mcuChannel, ChannelButton.SOLO));
            } else if (signal.button.name().endsWith("_Mute")) {
                proxy.sendEmulatorSignal(mcuBank, new ChannelButtonReleased(mcuChannel, ChannelButton.MUTE));
            // } else if (signal.button.name().endsWith("_Assign")) {
            // } else if (signal.button.name().endsWith("_Write")) {
            } else {
                System.err.println("Don't know what to do with " + signal);
            }
            return;
        }

    }

    public void dispatch(final FaderMove signal) {
        proxy.d8bFaderMove(signal.fader, signal.position);
    }

    public void dispatch(final SerialNumber signal) {
        if (!signal.serialNumber.equals(proxy.getD8BSerialNumber())) {
            System.err.println("D8B changed its serial number from "
                    + proxy.getD8BSerialNumber().toString(16)
                    + " to " + signal.serialNumber.toString(16)
                    + ", disregarding");
        }
    }

    public void dispatch(final Pong signal) {
        proxy.d8bPong();
    }

    public void dispatch(final Heartbeat1 signal) {
        proxy.d8bHeartbeat1();
    }

    public void dispatch(final Heartbeat2 signal) {
        proxy.d8bHeartbeat2();
    }

    public void dispatch(final JogLeft signal) {
        proxy.sendMasterEmulatorSignal(new com.adamdbradley.mcu.console.protocol.signal.JogLeft());
    }

    public void dispatch(final JogRight signal) {
        proxy.sendMasterEmulatorSignal(new com.adamdbradley.mcu.console.protocol.signal.JogRight());
    }

    public void dispatch(final VPotMove signal) {
        // TODO: use last timestamp of change on this VPot to scale velocity

        if (signal.vpot.hasFader()) {
            final Fader fader = signal.vpot.getFader();
            int signalPartition = 2 - (fader.ordinal() / 8);
            final com.adamdbradley.mcu.console.Fader signalFader = com.adamdbradley.mcu.console.Fader.values()[fader.ordinal() % 8];
            final com.adamdbradley.mcu.console.protocol.signal.ChannelVPotMoved emulatedSignal
                    = new com.adamdbradley.mcu.console.protocol.signal.ChannelVPotMoved(signalFader.channel(),
                            signal.direction == Direction.LEFT ? -1 : 1);
            proxy.sendEmulatorSignal(signalPartition, emulatedSignal);
        } else {
            System.err.println("No interpretation available for: " + signal);
        }
    }

    public void dispatch(final Signal signal) {
        System.err.println("Ignoring unknown d8b signal: " + signal);
    }

}
