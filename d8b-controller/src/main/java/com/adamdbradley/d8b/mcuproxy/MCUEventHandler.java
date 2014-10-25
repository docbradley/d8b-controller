
package com.adamdbradley.d8b.mcuproxy;

import java.math.BigInteger;

import com.adamdbradley.mcu.MCUEmulatorPort;
// Always use FQNs for d8b domain classes
import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.protocol.Message;
import com.adamdbradley.mcu.console.protocol.command.LightChannelLED;
import com.adamdbradley.mcu.console.protocol.command.LightPanelLED;
import com.adamdbradley.mcu.console.protocol.command.MoveFader;
import com.adamdbradley.mcu.console.protocol.command.Reboot1;
import com.adamdbradley.mcu.console.protocol.command.Reboot2;
import com.adamdbradley.mcu.console.protocol.command.RequestSerialNumber;
import com.adamdbradley.mcu.console.protocol.command.RequestVersion;
import com.adamdbradley.mcu.console.protocol.command.SetGlobalSignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.command.SetSignalLevel;
import com.adamdbradley.mcu.console.protocol.command.SetSignalLevelDisplayMode;
import com.adamdbradley.mcu.console.protocol.command.ShutChannelLED;
import com.adamdbradley.mcu.console.protocol.command.ShutPanelLED;
import com.adamdbradley.mcu.console.protocol.command.WakeUp;
import com.adamdbradley.mcu.console.protocol.command.WriteScreen;
import com.adamdbradley.mcu.console.protocol.command.WriteTimecode;
import com.adamdbradley.mcu.console.protocol.command.WriteVPot;
import com.adamdbradley.mcu.console.protocol.signal.FaderMoved;
import com.adamdbradley.mcu.console.protocol.signal.ReportSerialNumber;
import com.adamdbradley.mcu.console.protocol.signal.ReportVersion;
import com.adamdbradley.mcu.console.protocol.signal.ReportAwake;

//Class must be public so Application.typedDispatch can find methods
public class MCUEventHandler {

    private static final BigInteger MIDI_WORD_MASK = BigInteger.valueOf(0x7F);

    private final MCUEmulatorPort port;
    private final Proxy proxy;
    private final int position;
    private final DeviceType type;

    MCUEventHandler(final MCUEmulatorPort port, final int position, final Proxy proxy) {
        this.port = port;
        this.proxy = proxy;
        this.position = position;

        switch (position) {
        case 0:
            type = DeviceType.Master;
            break;
        case 1:
        case 2:
            type = DeviceType.Extender;
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public void dispatch(final Reboot1 command) {
        proxy.reboot(position);
    }

    public void dispatch(final Reboot2 command) {
        proxy.reboot(position);
    }

    public void dispatch(final LightChannelLED command) {
        final com.adamdbradley.d8b.console.PanelLED panelLED = ControllerMaps.map(position, command.channel, command.led);
        proxy.sendConsole(new com.adamdbradley.d8b.console.command.LightPanelLED(panelLED));
    }

    public void dispatch(final ShutChannelLED command) {
        final com.adamdbradley.d8b.console.PanelLED panelLED = ControllerMaps.map(position, command.channel, command.led);
        proxy.sendConsole(new com.adamdbradley.d8b.console.command.ShutPanelLED(panelLED));
    }

    public void dispatch(final LightPanelLED command) {
        if (position != 0) {
            System.err.println("Can't control a PanelLED on non-master device " + position);
            return;
        }

        final com.adamdbradley.d8b.console.PanelLED panelLED = ControllerMaps.mapToPanel(command.led);
        if (panelLED != null) {
            proxy.sendConsole(new com.adamdbradley.d8b.console.command.LightPanelLED(panelLED));
        } else {
            System.err.println("Can't turn on unmapped MCU " + command.led);
        }
    }

    public void dispatch(final ShutPanelLED command) {
        if (position != 0) {
            System.err.println("Can't control a PanelLED on non-master device " + position);
            return;
        }

        final com.adamdbradley.d8b.console.PanelLED panelLED = ControllerMaps.mapToPanel(command.led);
        if (panelLED != null) {
            proxy.sendConsole(new com.adamdbradley.d8b.console.command.ShutPanelLED(panelLED));
        } else {
            System.err.println("Can't shut off unmapped MCU " + command.led);
        }
    }

    /**
     * This could be a "spontaneous" move command, or it could be an
     * acknowledgment of a {@link FaderMoved} signal we sent previously.
     * @param position
     * @param command
     */
    public void dispatch(final MoveFader command) {
        proxy.commandFaderMove(position, command.fader, command.value);
    }

    /**
     * The d8b has a nominally 48-bit (12 hex digit) serial number;
     * mine (ADB) appears to use no more than the 28 least-significant bits, so
     * I (ADB) am going way out on a limb and assuming the most-significant-bits
     * are usually unused.
     * <p/>
     * The MCU serial number datagram has room for 49 digits (7 7-bit words).
     * We preserve the least-significant 42 bits of the d8b's serial number
     * as the first 6 words of our virtual MCU serial number, and use the
     * controller identifier (0=master, 1=XT1, 2=XT2, etc) as the seventh.
     * @param position
     * @param command
     */
    public void dispatch(final RequestSerialNumber command) {
        final BigInteger d8bSerial = proxy.getD8BSerialNumber();
        final ReportSerialNumber reply = new ReportSerialNumber(type,
                new byte[] {
                    d8bSerial.shiftRight(35).and(MIDI_WORD_MASK).byteValue(),
                    d8bSerial.shiftRight(28).and(MIDI_WORD_MASK).byteValue(),
                    d8bSerial.shiftRight(21).and(MIDI_WORD_MASK).byteValue(),
                    d8bSerial.shiftRight(14).and(MIDI_WORD_MASK).byteValue(),
                    d8bSerial.shiftRight(7).and(MIDI_WORD_MASK).byteValue(),
                    d8bSerial.shiftRight(0).and(MIDI_WORD_MASK).byteValue(),
                    Integer.valueOf(position).byteValue()
                });
        System.out.println("Sending " + position + "::" + reply);
        port.send(reply);
    }

    public void dispatch(final RequestVersion command) {
        final ReportVersion reply = new ReportVersion(type,
                new byte[] { 0x56, 0x34, 0x2e, 0x30, 0x32 }); // This is what my (ADB) MCU PRO sends...
        port.send(reply);
    }

    public void dispatch(final SetGlobalSignalLevelDisplayMode command) {
        for (int i=0; i<8; i++) {
            proxy.setSignalLevelMode(ControllerMaps.map(position, Fader.values()[i]), command.mode);
        }
    }

    public void dispatch(final SetSignalLevelDisplayMode command) {
        proxy.setSignalLevelMode(ControllerMaps.map(position, command.channel), command.mode);
    }

    public void dispatch(final SetSignalLevel command) {
        proxy.updateLevel(ControllerMaps.map(position, command.channel), command.value);
    }

    public void dispatch(final WakeUp command) {
        final ReportAwake reply = new ReportAwake(type,
                proxy.getInitId(position));
        port.send(reply);
    }

    public void dispatch(final WriteScreen command) {
        proxy.writeVirtualScreen(position, command);
    }

    public void dispatch(final WriteTimecode command) {
        if ((command.character < '0' || command.character > '9')
                && command.character != ' ') {
            System.err.println("Don't know how to put [" + command.character + "] in the Timecode");
            return;
        }
        final Integer parse;
        if (command.character == ' ') {
            parse = null;
        } else if (command.character == '-') {
            parse = -1;
        } else {
            parse = Integer.parseInt("" + command.character);
        }

        final com.adamdbradley.d8b.console.TimecodePosition mappedPosition;
        switch (command.position) {
        case 0:
            return; // 10-digit MCU LSD isn't representable in 8-digit d8b POSITION display
        case 9:
            return; // 10-digit MCU MSD isn't representable in 8-digit d8b POSITION display
        case 10:
            // Assignment LSD
            proxy.assignment = "" + proxy.assignment.charAt(0) + command.character;
            proxy.sendConsole(new com.adamdbradley.d8b.console.command.SetChannelNumber(proxy.assignment));
            return;
        case 11:
            // Assignment MSD
            proxy.assignment = "" + command.character + proxy.assignment.charAt(1);
            proxy.sendConsole(new com.adamdbradley.d8b.console.command.SetChannelNumber(proxy.assignment));
            return;
        default: // 1 - 8
            mappedPosition = com.adamdbradley.d8b.console.TimecodePosition
                    .values()[com.adamdbradley.d8b.console.TimecodePosition.RangeFromDigit1.ordinal() - command.position];
            proxy.sendConsole(new com.adamdbradley.d8b.console.command.UpdateTimecode(mappedPosition,
                    parse,
                    command.dot));
        }
    }

    public void dispatch(final WriteVPot command) {
        final com.adamdbradley.d8b.console.VPot vpot = com.adamdbradley.d8b.console.VPot
                .values()[ControllerMaps.map(position, command.channel).ordinal()];

        if (command.mode == null) {
            proxy.sendConsole(com.adamdbradley.d8b.console.command.VPotLEDHelper.clear(vpot, command.dot));
        } else switch (command.mode) {
        case SinglePosition:
            proxy.sendConsole(com.adamdbradley.d8b.console.command.VPotLEDHelper.singlePosition(vpot, command.value, command.dot));
            return;
        case SpreadFromCenter:
            proxy.sendConsole(com.adamdbradley.d8b.console.command.VPotLEDHelper.fanFromCenter(vpot, command.value, command.dot));
            return;
        case DirectionFanFromCenter:
            proxy.sendConsole(com.adamdbradley.d8b.console.command.VPotLEDHelper.directionalFan(vpot, command.value, command.dot));
            return;
        case FanFromLeft:
            proxy.sendConsole(com.adamdbradley.d8b.console.command.VPotLEDHelper.fanFromLeft(vpot, command.value, command.dot));
            return;
        default:
            System.err.println("Don't know how to display " + command);
        }
    }


    public void dispatch(final Message message) {
        System.err.println("Ignoring unhandled message: " + message);
    }


}
