package com.adamdbradley.d8b.console.command;

import java.util.List;

import org.apache.commons.io.Charsets;

import com.google.common.collect.ImmutableList;

/**
 * Helper methods for assembling commands to write to the screen.
 */
public class ScreenHelper {

    public static List<Command> buildStringCommands(final String string) {
        final ImmutableList.Builder<Command> builder = ImmutableList.builder();
        for (byte character: string.getBytes(Charsets.US_ASCII)) {
            builder.add(new WriteScreenCharacter(character));
        }
        return builder.build();
    }

    public static List<Command> buildDrawScreenCommands(final int row,
            final int column,
            final String string) {
        final ImmutableList.Builder<Command> builder = ImmutableList.builder();
        builder.add(new PositionCursor(row, column));
        builder.addAll(buildStringCommands(string));
        builder.add(new FlushScreen());
        return builder.build();
    }

}
