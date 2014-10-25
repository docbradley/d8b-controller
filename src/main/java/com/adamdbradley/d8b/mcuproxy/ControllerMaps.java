package com.adamdbradley.d8b.mcuproxy;

// MCU types are imported, D8B types use fully-qualified names
import com.adamdbradley.mcu.console.Channel;
import com.adamdbradley.mcu.console.ChannelLED;
import com.adamdbradley.mcu.console.Fader;
import com.adamdbradley.mcu.console.PanelButton;
import com.adamdbradley.mcu.console.PanelLED;

import com.google.common.collect.ImmutableBiMap;

public abstract class ControllerMaps {

    // Not constructable
    private ControllerMaps() {
    }


    static com.adamdbradley.d8b.console.PanelLED map(final int i,
            final Channel channel, final ChannelLED led) {
        return map(map(i, channel), led);
    }

    /**
     * Find the d8b PanelLED for the specified fader/led tuple.
     * @param map
     * @param led
     * @return
     */
    static com.adamdbradley.d8b.console.PanelLED map(final com.adamdbradley.d8b.console.Fader fader,
            final ChannelLED led) {
        final String name;
        switch (led) {
        case REC:
            name = "ch" + fader.ordinal() + "_nbch_RecReadyProxy";
            break;
        case SOLO:
            name = "ch" + fader.ordinal() + "_chp_Solo";
            break;
        case MUTE:
            name = "ch" + fader.ordinal() + "_chp_Mute";
            break;
        case SELECT:
            name = "ch" + fader.ordinal() + "_chp_Select";
            break;
        default:
            throw new IllegalArgumentException();
        }
        return com.adamdbradley.d8b.console.PanelLED.valueOf(name);
    }

    /**
     * Return the d8b Fader represented by the
     * {@link Channel} on the "i" controller.
     * @param i
     * @param channel
     * @return
     */
    static com.adamdbradley.d8b.console.Fader map(final int i, final Channel channel) {
        switch (i) {
        case 0:
            return com.adamdbradley.d8b.console.Fader.values()[channel.ordinal() + 16];
        case 1:
            return com.adamdbradley.d8b.console.Fader.values()[channel.ordinal() + 8];
        case 2:
            return com.adamdbradley.d8b.console.Fader.values()[channel.ordinal() + 0];
        default:
            throw new IllegalArgumentException();
        }
    }

    static com.adamdbradley.d8b.console.Fader map(final int i, final Fader fader) {
        if (fader == Fader.Master) {
            return com.adamdbradley.d8b.console.Fader.Master;
        } else {
            return map(i, fader.channel());
        }
    }


    // For the most part, the D8B has an identical set of LEDs and Buttons.
    // EXCEPTION: RUDE SOLO

    // For the most part, the MCU LEDs are a SUBSET of the Buttons.
    // EXCEPTIONS: SMPTE and BEATS timecode indicators

    /**
     * Map from a requested MCU LED to a physical d8b LED.
     * @param led
     * @return
     */
    public static com.adamdbradley.d8b.console.PanelLED mapToPanel(final PanelLED led) {
        // MCU doesn't have a RUDE SOLO LED, so the LEDs we're interested in
        // are a proper subset of buttons on both sides, so just use the buttons.
        if (led == PanelLED.Timecode_SMPTE) {
            return com.adamdbradley.d8b.console.PanelLED.vtransportproxy_ShowSmpte;
        } else if (led == PanelLED.Timecode_BEATS) {
            return com.adamdbradley.d8b.console.PanelLED.vtransportproxy_SetTime;
        } else {
            final PanelButton button = PanelButton.valueOf(led.name());
            final com.adamdbradley.d8b.console.PanelButton mappedButton = panelButtonMap.inverse().get(button);
            if (mappedButton != null) {
                return com.adamdbradley.d8b.console.PanelLED.valueOf(mappedButton.name());
            } else {
                return null;
            }
        }
    }

    public static PanelButton mapToPanel(final com.adamdbradley.d8b.console.PanelButton button) {
        return panelButtonMap.get(button);
    }


    private static final ImmutableBiMap<com.adamdbradley.d8b.console.PanelButton, PanelButton> panelButtonMap =
            ImmutableBiMap.<com.adamdbradley.d8b.console.PanelButton, PanelButton>builder()
                    // MCU "View" buttons
                    .put(com.adamdbradley.d8b.console.PanelButton.vfdproxy_fatload, PanelButton.Track_Bus) // "Load" means "Track/Bus"
                    .put(com.adamdbradley.d8b.console.PanelButton.vfdproxy_fatsave, PanelButton.Send) // "Save" means "Send"
                    .put(com.adamdbradley.d8b.console.PanelButton.vfdproxy_fatgate, PanelButton.Pan) // "Gate" means "Pan"
                    .put(com.adamdbradley.d8b.console.PanelButton.vfdproxy_fatplugin, PanelButton.Plugin)
                    .put(com.adamdbradley.d8b.console.PanelButton.vfdproxy_fateq, PanelButton.Eq)
                    .put(com.adamdbradley.d8b.console.PanelButton.vfdproxy_fatcompress, PanelButton.Compressor)

                    // MCU "Fader Banks" buttons
                    .put(com.adamdbradley.d8b.console.PanelButton.ch96_ch_Select, PanelButton.BankLeft)
                    .put(com.adamdbradley.d8b.console.PanelButton.global_control, PanelButton.BankRight)
                    .put(com.adamdbradley.d8b.console.PanelButton.ch96_ch_Write, PanelButton.ChannelLeft)
                    .put(com.adamdbradley.d8b.console.PanelButton.global_option, PanelButton.ChannelRight)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Flip)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Edit)

                    // MCU "Display" buttons
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Display_Name_Value)
                    .put(com.adamdbradley.d8b.console.PanelButton.vtransportproxy_ShowSmpte, PanelButton.Display_SMPTE_Beats)

                    // MCU Function buttons
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.F1_Cut)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.F2_Copy)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.F3_Paste)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.F4_Delete)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.F5_Space)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.F6_Alt)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.F7_Tab)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.F8_Back)

                    // MCU Tracks buttons
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Tracks_NewAudio)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Tracks_NewMidi)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Tracks_FitTracks)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Tracks_FitProject)

                    // MCU Dialog buttons
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Dialog_OkEnter)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Dialog_Cancel)

                    // MCU Window buttons
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Window_Next)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Window_Close)

                    // MCU Modifiers
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Modifiers_M1)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Modifiers_M2)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Modifiers_M3)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Modifiers_M4)

                    // MCU Automation buttons
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Automation_Read_Off)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Automation_Snapshot)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Automation_Disarm)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Automation_Offset)

                    // MCU Control Group
                    .put(com.adamdbradley.d8b.console.PanelButton.vstrip_FaderDeck0, PanelButton.ControlGroup_Track)
                    .put(com.adamdbradley.d8b.console.PanelButton.vstrip_FaderDeck1, PanelButton.ControlGroup_Aux)
                    .put(com.adamdbradley.d8b.console.PanelButton.vstrip_FaderDeck2, PanelButton.ControlGroup_Main)

                    // MCU Editing
                    .put(com.adamdbradley.d8b.console.PanelButton.vfd_save, PanelButton.Save)
                    .put(com.adamdbradley.d8b.console.PanelButton.vstripproxy_undo, PanelButton.Undo)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Redo)

                    // MCU In/Out
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Marker)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Loop)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Select)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Punch)

                    // MCU Transport helpers
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.JogParam)
                    .put(com.adamdbradley.d8b.console.PanelButton.vtransportproxy_To, PanelButton.LoopOnOff)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Home)

                    // MCU Transport
                    .put(com.adamdbradley.d8b.console.PanelButton.vtransportproxy_Rewind, PanelButton.Rewind)
                    .put(com.adamdbradley.d8b.console.PanelButton.vtransportproxy_FastForward, PanelButton.FastFwd)
                    .put(com.adamdbradley.d8b.console.PanelButton.vtransportproxy_Stop, PanelButton.Stop)
                    .put(com.adamdbradley.d8b.console.PanelButton.vtransportproxy_Play, PanelButton.Play)
                    .put(com.adamdbradley.d8b.console.PanelButton.vtransportproxy_RecWrite, PanelButton.Record)
                    .put(com.adamdbradley.d8b.console.PanelButton.vtransportproxy_jogshuttle, PanelButton.Scrub)

                    // MCU zoom
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Zoom)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Up)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Down)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Left)
                    //.put(com.adamdbradley.d8b.console.PanelButton.nil, PanelButton.Right)

                    .build();

}
