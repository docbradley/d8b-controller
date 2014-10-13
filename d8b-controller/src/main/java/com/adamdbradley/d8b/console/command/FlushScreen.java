package com.adamdbradley.d8b.console.command;

/**
 * Flush the screen buffer to the physical screen.
 * This (preceded by {@link CommandType#ClearScreen})
 * does strange things to the writability of the screen;
 * better to avoid them altogether once the boot sequence
 * is over.
 */
public class FlushScreen extends UnparameterizedCommand {

    public FlushScreen() {
        super(CommandType.FlushScreen);
    }

}
