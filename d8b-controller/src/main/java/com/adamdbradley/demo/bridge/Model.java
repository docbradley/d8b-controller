package com.adamdbradley.demo.bridge;

import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.d8b.console.PanelLED;
import com.adamdbradley.d8b.console.command.LightPanelLED;
import com.adamdbradley.d8b.console.command.ShutPanelLED;
import com.adamdbradley.mcu.MCUClientPort;
import com.adamdbradley.mcu.console.ChannelLED;
import com.adamdbradley.mcu.console.DeviceType;
import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.protocol.command.LightChannelLED;
import com.adamdbradley.mcu.console.protocol.command.MoveFader;
import com.adamdbradley.mcu.console.protocol.command.ShutChannelLED;
import com.adamdbradley.mcu.console.protocol.command.WriteScreen;
import com.adamdbradley.mcu.console.protocol.command.WriteVPot;
import com.adamdbradley.mcu.console.protocol.command.WriteVPot.VPotMode;

public class Model {

    public int d8bViewOffset = 0;
    public int mcuViewOffset = 0;



    private ConsoleControlConnection console;
    private MCUClientPort mcuPort;

    public void connect(ConsoleControlConnection console, MCUClientPort mcuPort) {
        this.console = console;
        this.mcuPort = mcuPort;
    }

    public final Channel[] channels;

    public int masterVpotValue;
    public VPotMode masterVpotMode;
    public int masterFader;

    public Model() {
        channels = new Channel[48];
        for (int i=0; i<channels.length; i++) {
            channels[i] = new Channel();
        }
    }

    public class Channel {
        public int vpotValue;
        public VPotMode vpotMode = VPotMode.SinglePosition;
        public boolean record;
        public boolean assign;
        public boolean write;
        public boolean green;
        public boolean red;
        public boolean select;
        public boolean solo;
        public boolean mute;

        // 0 - 1023
        public int fader;
    }

    public boolean record;
    public TransportMode transportMode = TransportMode.Stopped;
    public enum TransportMode {
        Stopped,
        Playing,
        Paused,
        Shuttling,
        Rewinding,
        FastForwarding
    }

    public void rewind(boolean b) {
        if (b) {
            if (transportMode == TransportMode.Stopped) {
                transportMode = TransportMode.Rewinding;
                displayTransportMode();
            }
        } else {
            if (transportMode == TransportMode.Rewinding) {
                transportMode = TransportMode.Stopped;
                displayTransportMode();
            }
        }
    }

    public void fastFwd(boolean b) {
        if (b) {
            if (transportMode == TransportMode.Stopped) {
                transportMode = TransportMode.FastForwarding;
                displayTransportMode();
            }
        } else {
            if (transportMode == TransportMode.FastForwarding) {
                transportMode = TransportMode.Stopped;
                displayTransportMode();
            }
        }
    }

    public void stop(boolean b) {
        if (b) {
            transportMode = TransportMode.Stopped;
            displayTransportMode();
        }
    }

    public void play(boolean b) {
        if (b) {
            if (transportMode == TransportMode.Stopped || transportMode == TransportMode.Paused) {
                transportMode = TransportMode.Playing;
                displayTransportMode();
            } else if (transportMode == TransportMode.Playing) {
                transportMode = TransportMode.Paused;
                displayTransportMode();
            }
        }
    }

    public void record(boolean b) {
        if (b) {
            this.record = !this.record;
            displayTransportMode();
        }
    }

    private void displayTransportMode() {
        if (record) {
            console.send(new LightPanelLED(PanelLED.vtransportproxy_RecWrite));
            mcuPort.send(new com.adamdbradley.mcu.console.protocol.command.LightPanelLED(com.adamdbradley.mcu.console.PanelLED.Record));
        } else {
            console.send(new ShutPanelLED(PanelLED.vtransportproxy_RecWrite));
            mcuPort.send(new com.adamdbradley.mcu.console.protocol.command.ShutPanelLED(com.adamdbradley.mcu.console.PanelLED.Record));
        }

        switch (transportMode) {
        case Rewinding:
            // Rew lit
        case FastForwarding:
            // FF lit
        case Paused:
            // Play blinking (or just lit if blink not native)
        case Playing:
            // Play lit
        case Shuttling:
            // Stop lit
        case Stopped:
            // Nothing
        }
    }

    public void channelRec(int i) {
        channels[i].record = !channels[i].record;
        displayChannel(i);
    }

    public void channelSolo(int i) {
        channels[i].solo = !channels[i].solo;
        displayChannel(i);
    }

    public void channelMute(int i) {
        channels[i].mute = !channels[i].mute;
        displayChannel(i);
    }

    public void channelSelect(int i) {
        channels[i].select = !channels[i].select;
        displayChannel(i);
    }

    public void channelVPot(int i) {
        channels[i].vpotMode = VPotMode.values()[(channels[i].vpotMode.ordinal() + 1) % VPotMode.values().length];
        channels[i].vpotValue = 0;
        displayChannel(i);
    }

    public void channelVPot(int i, int velocity) {
        if (velocity > 0) {
            channels[i].vpotValue++;
        } else {
            channels[i].vpotValue--;
        }
        displayChannel(i);
    }

    public void mcuMove(int adj) {
        mcuViewOffset = Math.max(0,
                Math.min(
                        channels.length - 8,
                        mcuViewOffset + adj));
        System.err.println("MCU adv: " + mcuViewOffset);
        for (int i=0; i<8; i++) {
            displayChannelMCU(mcuViewOffset + i);
        }
    }

    /**
     * Range 0 - 1023
     * @param i
     * @param value
     */
    public void fader(int i, int value) {
        if (channels[i].fader != value) {
            System.err.println("Moving " + i);
            channels[i].fader = value;
            if (inViewD8B(i)) {
                console.send(new com.adamdbradley.d8b.console.command.MoveFader(com.adamdbradley.d8b.console.Fader.values()[i - d8bViewOffset], value >> 2));
            }
            if (inViewMCU(i)) {
                mcuPort.send(new MoveFader(Fader.values()[i - mcuViewOffset], value));
            }
        }
    }

    public void masterFader(int value) {
        if (masterFader != value) {
            masterFader = value;
            console.send(new com.adamdbradley.d8b.console.command.MoveFader(com.adamdbradley.d8b.console.Fader.Master, value >> 2));
            mcuPort.send(new MoveFader(Fader.Master, value));
        }
    }

    private boolean inViewMCU(final int i) {
        return (i >= mcuViewOffset && i < mcuViewOffset + 8);
    }

    private boolean inViewD8B(final int i) {
        return (i >= d8bViewOffset && i < d8bViewOffset + 24);
    }







    private void displayChannel(int i) {
        if (inViewMCU(i)) {
            displayChannelMCU(i);
        }
        if (inViewD8B(i)) {
            displayChannelD8B(i);
            
        }
    }

    private void displayChannelMCU(int modelChannel) {
        final Model.Channel channel = channels[modelChannel];

        final int pos = modelChannel - mcuViewOffset;

        final com.adamdbradley.mcu.console.Channel strip = com.adamdbradley.mcu.console.Channel.values()[pos];
        final Fader fader = strip.fader();

        mcuPort.send(new WriteScreen(DeviceType.Master,
                0, pos * 7, "       "));
        mcuPort.send(new WriteScreen(DeviceType.Master,
                0, pos * 7, Integer.toString(modelChannel)));

        final String status = ""
                + (channel.assign ? 'A' : ' ')
                + (channel.green ? (channel.red ? 'X' : '^') : (channel.record ? 'v' : ' '))
                + (channel.write ? 'W' : ' ')
                ;
        mcuPort.send(new WriteScreen(DeviceType.Master, 1, pos * 7, status));

        mcuPort.send(new MoveFader(fader, channel.fader));
        if (channel.record) {
            mcuPort.send(new LightChannelLED(strip, ChannelLED.REC));
        } else {
            mcuPort.send(new ShutChannelLED(strip, ChannelLED.REC));
        }
        if (channel.solo) {
            mcuPort.send(new LightChannelLED(strip, ChannelLED.SOLO));
        } else {
            mcuPort.send(new ShutChannelLED(strip, ChannelLED.SOLO));
        }
        if (channel.mute) {
            mcuPort.send(new LightChannelLED(strip, ChannelLED.MUTE));
        } else {
            mcuPort.send(new ShutChannelLED(strip, ChannelLED.MUTE));
        }
        if (channel.select) {
            mcuPort.send(new LightChannelLED(strip, ChannelLED.SELECT));
        } else {
            mcuPort.send(new ShutChannelLED(strip, ChannelLED.SELECT));
        }
        mcuPort.send(new WriteVPot(strip, channel.vpotMode, channel.vpotValue, false));
    }

    private void displayChannelD8B(int i) {
        final Model.Channel channel = channels[mcuViewOffset + i];

        final com.adamdbradley.d8b.console.Fader fader = com.adamdbradley.d8b.console.Fader.values()[i - d8bViewOffset];
        console.send(new com.adamdbradley.d8b.console.command.MoveFader(fader,
                channel.fader >> 2));
    }

}
