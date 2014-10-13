package com.adamdbradley.d8b.console;

/**
 * Names are a recognizable distortion of their format in
 * the "switch.txt" file, which must be in the "/firmware/"
 * directory for {@link ConsoleIdMaps} to load correctly.
 * <p/>
 * TODO: Needs a better model, there's a lot of logical
 * subgrouping and needless per-channel repetition.
 * Probably use a top-level "PanelButton" marker interface
 * that's implemented by multiple enums as well as a
 * channel-enum tuple class.
 */
public enum PanelButton implements ConsoleIdMaps.Aliased {

    dcapanel_MonLRMix,
    dcapanel_MondigitalIn1,
    dcapanel_MondigitalIn2,
    dcapanel_Mon2trackA,
    dcapanel_Mon2trackB,
    dcapanel_Mon2trackC,
    dcapanel_Mono,
    dcapanel_NearFieldSwitch,
    dcapanel_MainSwitch,
    dcapanel_Dim,
    dcapanel_TalkbackSwitch,
    dcapanel_SoloLevelSwitch,
    dcapanel_StudioSwitch,
    dcapanel_TalkToStudio,
    dcapanel_TalkbackPressed,
    dcapanel_MixdownSolo,
    dcapanel_PFLSolo,
    dcapanel_AFLSolo,
    dcapanel_ClearSolo,
    dcapanel_Phones1Cue1,
    dcapanel_Phones1Cue2,
    dcapanel_Phones1CRoom,
    dcapanel_Phones2Cue1,
    dcapanel_Phones2Cue2,
    dcapanel_Phones2CRoom,
    dcapanel_fadermotorsoff,
    dcapanel_HelpSwitch,
    vfd_saveas,
    vfd_new,
    vfd_load,
    vfdproxy_fatload,
    vfd_mixtocue1,
    vfd_mixtocue2,
    vfdproxy_fatsave,
    vfd_save,
    vfd_group,
    vfd_general,
    vfd_plugins,
    vfd_digitalio,
    vfdproxy_fateq,
    vfdproxy_fatgate,
    vfdproxy_fatcompress,
    vfdproxy_fatplugin,
    vfdproxy_eqsetup,
    vstripproxy_copy,
    vstripproxy_cut,
    vstripproxy_paste,
    vstripproxy_undo,
    vtransportproxy_Stop,
    vtransportproxy_Play,
    vtransportproxy_Rewind,
    vtransportproxy_FastForward,
    vtransportproxy_RecWrite,
    vtransportproxy_ShowSmpte,
    vtransportproxy_jogshuttle,
    vstripproxy_AutomationModeBypass,
    vstripproxy_AutomationModeTrim,
    vstripproxy_AutomationEnabeFader,
    vstripproxy_AutomationEnableMute,
    vstripproxy_AutomationEnableAll,
    vstripproxy_AutomationModeTouch,
    vstripproxy_AutomationEnablePan,
    vtransportproxy_Store,
    vtransportproxy_keypadenter,
    vtransportproxy_SetTime,
    vtransportproxy_To,
    vtransportproxy_keypad0,
    vtransportproxy_keypad1,
    vtransportproxy_keypad2,
    vtransportproxy_keypad3,
    vtransportproxy_keypad4,
    vtransportproxy_keypad5,
    vtransportproxy_keypad6,
    vtransportproxy_keypad7,
    vtransportproxy_keypad8,
    vtransportproxy_keypad9,
    vtransportproxy_Snapshot,
    vtransportproxy_Locate,
    nbch_RecReadyProxy,
    ch0_chp_Select,
    ch0_chp_Solo,
    ch0_chp_Mute,
    ch0_chp_Assign,
    ch0_chp_Write,
    ch1_nbch_RecReadyProxy,
    ch1_chp_Select,
    ch1_chp_Solo,
    ch1_chp_Mute,
    ch1_chp_Assign,
    ch1_chp_Write,
    ch2_nbch_RecReadyProxy,
    ch2_chp_Select,
    ch2_chp_Solo,
    ch2_chp_Mute,
    ch2_chp_Assign,
    ch2_chp_Write,
    ch3_nbch_RecReadyProxy,
    ch3_chp_Select,
    ch3_chp_Solo,
    ch3_chp_Mute,
    ch3_chp_Assign,
    ch3_chp_Write,
    ch4_nbch_RecReadyProxy,
    ch4_chp_Select,
    ch4_chp_Solo,
    ch4_chp_Mute,
    ch4_chp_Assign,
    ch4_chp_Write,
    ch5_nbch_RecReadyProxy,
    ch5_chp_Select,
    ch5_chp_Solo,
    ch5_chp_Mute,
    ch5_chp_Assign,
    ch5_chp_Write,
    ch6_nbch_RecReadyProxy,
    ch6_chp_Select,
    ch6_chp_Solo,
    ch6_chp_Mute,
    ch6_chp_Assign,
    ch6_chp_Write,
    ch7_nbch_RecReadyProxy,
    ch7_chp_Select,
    ch7_chp_Solo,
    ch7_chp_Mute,
    ch7_chp_Assign,
    ch7_chp_Write,
    ch8_nbch_RecReadyProxy,
    ch8_chp_Select,
    ch8_chp_Solo,
    ch8_chp_Mute,
    ch8_chp_Assign,
    ch8_chp_Write,
    ch9_nbch_RecReadyProxy,
    ch9_chp_Select,
    ch9_chp_Solo,
    ch9_chp_Mute,
    ch9_chp_Assign,
    ch9_chp_Write,
    ch10_nbch_RecReadyProxy,
    ch10_chp_Select,
    ch10_chp_Solo,
    ch10_chp_Mute,
    ch10_chp_Assign,
    ch10_chp_Write,
    ch11_nbch_RecReadyProxy,
    ch11_chp_Select,
    ch11_chp_Solo,
    ch11_chp_Mute,
    ch11_chp_Assign,
    ch11_chp_Write,
    ch12_nbch_RecReadyProxy,
    ch12_chp_Select,
    ch12_chp_Solo,
    ch12_chp_Mute,
    ch12_chp_Assign,
    ch12_chp_Write,
    ch13_nbch_RecReadyProxy,
    ch13_chp_Select,
    ch13_chp_Solo,
    ch13_chp_Mute,
    ch13_chp_Assign,
    ch13_chp_Write,
    ch14_nbch_RecReadyProxy,
    ch14_chp_Select,
    ch14_chp_Solo,
    ch14_chp_Mute,
    ch14_chp_Assign,
    ch14_chp_Write,
    ch15_nbch_RecReadyProxy,
    ch15_chp_Select,
    ch15_chp_Solo,
    ch15_chp_Mute,
    ch15_chp_Assign,
    ch15_chp_Write,
    ch16_nbch_RecReadyProxy,
    ch16_chp_Select,
    ch16_chp_Solo,
    ch16_chp_Mute,
    ch16_chp_Assign,
    ch16_chp_Write,
    ch17_nbch_RecReadyProxy,
    ch17_chp_Select,
    ch17_chp_Solo,
    ch17_chp_Mute,
    ch17_chp_Assign,
    ch17_chp_Write,
    ch18_nbch_RecReadyProxy,
    ch18_chp_Select,
    ch18_chp_Solo,
    ch18_chp_Mute,
    ch18_chp_Assign,
    ch18_chp_Write,
    ch19_nbch_RecReadyProxy,
    ch19_chp_Select,
    ch19_chp_Solo,
    ch19_chp_Mute,
    ch19_chp_Assign,
    ch19_chp_Write,
    ch20_nbch_RecReadyProxy,
    ch20_chp_Select,
    ch20_chp_Solo,
    ch20_chp_Mute,
    ch20_chp_Assign,
    ch20_chp_Write,
    ch21_nbch_RecReadyProxy,
    ch21_chp_Select,
    ch21_chp_Solo,
    ch21_chp_Mute,
    ch21_chp_Assign,
    ch21_chp_Write,
    ch22_nbch_RecReadyProxy,
    ch22_chp_Select,
    ch22_chp_Solo,
    ch22_chp_Mute,
    ch22_chp_Assign,
    ch22_chp_Write,
    ch23_nbch_RecReadyProxy,
    ch23_chp_Select,
    ch23_chp_Solo,
    ch23_chp_Mute,
    ch23_chp_Assign,
    ch23_chp_Write,
    vstripproxy_aux1,
    vstripproxy_aux2,
    vstripproxy_aux3,
    vstripproxy_aux4,
    vstripproxy_aux5,
    vstripproxy_aux6,
    vstripproxy_aux7,
    vstripproxy_aux8,
    vstripproxy_cue1pan,
    vstripproxy_cue1level,
    vstripproxy_cue2pan,
    vstripproxy_cue2level,
    vstripproxy_pan,
    vstripproxy_TapeOut,
    vstripproxy_DigitalTrim,
    vstripproxy_solo,
    vstrip_FaderDeck_0,
    vstrip_FaderDeck_1,
    vstrip_FaderDeck_2,
    vstrip_FaderDeck_3,
    vstripproxy_bus1,
    vstripproxy_bus2,
    vstripproxy_bus3,
    vstripproxy_bus4,
    vstripproxy_bus5,
    vstripproxy_bus6,
    vstripproxy_bus7,
    vstripproxy_bus8,
    vstripproxy_BusLR,
    vstripproxy_DirectRoute,
    vfdmanager_menuleft,
    vfdmanager_menuright,
    vfdmanager_vswitch1,
    vfdmanager_vswitch2,
    vfdmanager_vswitch3,
    vfdmanager_vswitch4,
    vfdproxy_on,
    vfdproxy_MemoryA,
    vfdproxy_MemoryB,
    global_option,
    global_control,
    global_shift,
    ch96_ch_Select,
    ch96_ch_Write;

    public String[] aliases() {
        return new String[] { name() };
    }

}
