package com.adamdbradley.mcu.console.protocol.signal;

import java.util.Map;

import com.adamdbradley.mcu.console.PanelButton;
import com.adamdbradley.mcu.console.protocol.NoteOnMessageBase;
import com.adamdbradley.mcu.console.protocol.Signal;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public abstract class ButtonMessage
extends NoteOnMessageBase
implements Signal {

    public final PanelButton button;

    public ButtonMessage(final PanelButton button, boolean pressed) {
        super(/* channel always 0 */(byte) 0x00,
                encode(button),
                (byte) ((pressed) ? 0x7F : 0x00));
        this.button = button;
    }

    private static final Map<PanelButton, Byte> encode;
    private static final Map<Byte, PanelButton> decode;
    static {
        final ImmutableBiMap.Builder<PanelButton, Byte> codecBuilder = ImmutableBiMap.builder();
        for (final PanelButton button: PanelButton.values()) {
            codecBuilder.put(button, byteForButton(button));
        }
        final BiMap<PanelButton, Byte> codec = codecBuilder.build();
        encode = codec;
        decode = codec.inverse();
    }

    public static byte encode(final PanelButton b) {
        return encode.get(b);
    }

    public static PanelButton decode(final byte b) {
        return decode.get(b);
    }


    private static byte byteForButton(final PanelButton button) {
        switch (button) {
        case Track_Bus: // 0x28
        case Send:
        case Pan:
        case Plugin:
        case Eq:
        case Compressor:
        case BankLeft:
        case BankRight:
        case ChannelLeft: // 0x30
        case ChannelRight:
        case Flip:
        case Edit:
        case Display_Name_Value:
        case Display_SMPTE_Beats:
        case F1_Cut:
        case F2_Copy:
        case F3_Paste: // 0x38
        case F4_Delete:
        case F5_Space:
        case F6_Alt:
        case F7_Tab:
        case F8_Back:
        case Tracks_NewAudio:
        case Tracks_NewMidi:
        case Tracks_FitTracks: // 0x40
        case Tracks_FitProject:
        case Dialog_OkEnter:
        case Dialog_Cancel:
        case Window_Next:
        case Window_Close:
        case Modifiers_M1:
        case Modifiers_M2:
        case Modifiers_M3: // 0x48
        case Modifiers_M4:
        case Automation_Read_Off:
        case Automation_Snapshot:
            return (byte) (0x28 + button.ordinal());
        case Automation_Disarm:
            return 0x4D;
        case Automation_Offset:
            return 0x4E;
        case ControlGroup_Track:
            return 0x4C;
        case ControlGroup_Aux:
            return 0x50;
        case ControlGroup_Main:
            return 0x51;
        case Save:
            return 0x4F;
        case Undo:
        case Redo:
        case Marker:
        case Loop:
        case Select:
        case Punch:
        case JogParam: // 0x58
        case LoopOnOff:
        case Home:
        case Rewind:
        case FastFwd:
        case Stop:
        case Play:
        case Record:
            return (byte) (0x28 + button.ordinal());
        case Up:
            return 0x60;
        case Left:
            return 0x62;
        case Zoom:
            return 0x64;
        case Right:
            return 0x63;
        case Down:
            return 0x61;
        case Scrub:
            return 0x65;
        }
        throw new RuntimeException("Unknown " + button);
    }

}
