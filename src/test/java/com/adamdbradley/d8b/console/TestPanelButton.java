package com.adamdbradley.d8b.console;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestPanelButton {

    @Test
    public void channelButtonMapping() throws Exception {
        assertTrue(PanelButton.ch0_chp_Assign.isChannelButton());
        assertEquals(Fader.Ch1, PanelButton.ch0_chp_Assign.getChannel());

        assertTrue(PanelButton.ch23_chp_Assign.isChannelButton());
        assertEquals(Fader.Ch24, PanelButton.ch23_chp_Assign.getChannel());
    }

    @Test
    public void nonChannelButtons() throws Exception {
        assertFalse(PanelButton.ch96_ch_Select.isChannelButton());
        assertFalse(PanelButton.ch96_ch_Write.isChannelButton());

        assertFalse(PanelButton.dcapanel_AFLSolo.isChannelButton());
    }
}
