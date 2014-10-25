package com.adamdbradley.d8b.mcuproxy;

import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.d8b.console.command.ScreenHelper;
import com.adamdbradley.mcu.console.protocol.command.WriteScreen;
import com.google.common.base.Charsets;

/**
 * The screens of the three virtual MCU devices,
 * presented in a sliding view on the d8b's screen.
 */
class VirtualScreen {

    private final ConsoleControlConnection console;
    private final int logicalScreens;
    private final int logicalScreenHeight;
    private final int logicalScreenWidth;
    private final int viewportWidth;
    private final int incrementSize;

    private int virtualScreenOffset = 0;
    private char[][][] virtualScreens;

    public VirtualScreen(final ConsoleControlConnection console,
            final int logicalScreens,
            final int logicalScreenWidth,
            final int logicalScreenHeight,
            final int viewportWidth,
            final int incrementSize) {
        this.console = console;
        this.logicalScreens = logicalScreens;
        this.logicalScreenHeight = logicalScreenHeight;
        this.logicalScreenWidth = logicalScreenWidth;
        this.viewportWidth = viewportWidth;
        this.incrementSize = incrementSize;
        this.virtualScreens = new char[logicalScreens][logicalScreenHeight][logicalScreenWidth];
        for (int i=0; i<logicalScreens; i++) {
            for (int j=0; j<logicalScreenHeight; j++) {
                for (int k=0; k<logicalScreenWidth; k++) {
                    virtualScreens[i][j][k] = ' ';
                }
            }
        }
    }

    void write(int i, int row, int column, String string) {
        final int rationalBasePos = (row * logicalScreenWidth) + column;
        final char[][] virtualScreen = virtualScreens[2 - i];

        final byte[] input = string.getBytes(Charsets.US_ASCII);
        for (int c=0; c<string.length(); c++) {
            final int rationalCharPos = rationalBasePos + c;
            final int rationalCharRow = rationalCharPos / logicalScreenWidth;
            final int rationalCharColumn = rationalCharPos % logicalScreenWidth;
            // XXX TODO XXX -- this should've been decoded when we parsed the message
            virtualScreen[rationalCharRow][rationalCharColumn] = WriteScreen
                    .decode(input[c]);
        }

        paint();
    }

    public void shiftLeft() {
        virtualScreenOffset = Math.min(virtualScreenOffset + incrementSize,
                (logicalScreenWidth * logicalScreens) - viewportWidth);
        paint();
    }

    public void shiftRight() {
        virtualScreenOffset = Math.max(virtualScreenOffset - incrementSize,
                0);
        paint();
    }




    /**
     * Redraw the virtual screen, including the arrows ("<" or ">") on either
     * side and the pipewall ("|") that's the right boundary.
     * NOT responsible for screen real estate beyond the right boundary.
     */
    public void paint() {
        final char[][] viewport = new char[logicalScreenHeight][viewportWidth + 1];
        for (int viewportColumn=0; viewportColumn<viewportWidth; viewportColumn++) {
            int logicalColumn = viewportColumn + virtualScreenOffset;
            if (logicalColumn < logicalScreenWidth) {
                // TODO: loop over logicalScreenHeight
                viewport[0][viewportColumn] = virtualScreens[0][0][logicalColumn];
                viewport[1][viewportColumn] = virtualScreens[0][1][logicalColumn];
            } else if (logicalColumn < logicalScreenWidth * 2) {
                // TODO: loop over logicalScreenHeight
                viewport[0][viewportColumn] = virtualScreens[1][0][logicalColumn - logicalScreenWidth];
                viewport[1][viewportColumn] = virtualScreens[1][1][logicalColumn - logicalScreenWidth];
            } else {
                // TODO: loop over logicalScreenHeight
                viewport[0][viewportColumn] = virtualScreens[2][0][logicalColumn - (logicalScreenWidth * 2)];
                viewport[1][viewportColumn] = virtualScreens[2][1][logicalColumn - (logicalScreenWidth * 2)];
            }
            if (virtualScreenOffset > 0) {
                viewport[0][0] = '<';
                viewport[1][0] = '<';
            }
            if (virtualScreenOffset < (logicalScreenWidth * 3) - viewportWidth) {
                viewport[0][viewportWidth - 1] = '>';
                viewport[1][viewportWidth - 1] = '>';
            }
            viewport[0][viewportWidth] = '|';
            viewport[1][viewportWidth] = '|';
        }

        console.send(ScreenHelper.buildDrawScreenCommands(0, 0, new String(viewport[0])));
        console.send(ScreenHelper.buildDrawScreenCommands(1, 0, new String(viewport[1])));
    }


}
