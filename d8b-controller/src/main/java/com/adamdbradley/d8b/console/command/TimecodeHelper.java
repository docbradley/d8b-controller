package com.adamdbradley.d8b.console.command;

import java.util.List;
import java.util.Stack;

import com.adamdbradley.d8b.console.TimecodePosition;
import com.google.common.collect.ImmutableList;

/**
 * Helper methods for rewriting the timecode display.
 */
public class TimecodeHelper {

    /**
     * Format is
     * <pre>
     * ([0-9 -]\.?){0,12}
     * </pre>
     * Where digits are digits, space is a blank (off) digit,
     * dash ('-') is a pause digit, period ('.') sets the "dot"
     * LED after the previous digit.  The timecode display is
     * built up from the left, so if you only specify four digits
     * "1234", these will be shown in the LOOP section with
     * "FROM" as "12" and "TO" as "34".
     * @param string
     * @return
     */
    public static List<Command> buildRedrawTimecodeCommands(final String string) {
        if (string.replaceAll("\\.",  "").length() > 12) {
            throw new IllegalArgumentException("Can't fit");
        }

        Stack<TimecodeDigit> stack = new Stack<>();

        for (char character: string.toCharArray()) {
            if (character == '.') {
                stack.peek().dot();
            } else {
                stack.push(new TimecodeDigit(character));
            }
        }

        final ImmutableList.Builder<Command> result = ImmutableList.<Command>builder();
        for (int position = 11; position >= 0; position--) {
            if (stack.isEmpty()) {
                result.add(new UpdateTimecode(TimecodePosition.values()[position], null, false));
            } else {
                TimecodeDigit digit = stack.pop();
                result.add(new UpdateTimecode(TimecodePosition.values()[position], digit.value(), digit.isDotted));
            }
        }
        return result.build();
    }

    private static class TimecodeDigit {
        // Always available
        private boolean isDotted = false;

        // Supersedes #isPause
        private boolean isBlank = false;

        // Supersedes #digit
        private boolean isPause = false;

        // 0-9
        private int digit = -1;

        TimecodeDigit(char character) {
            this((byte) character);
        }

        TimecodeDigit(byte character) {
            switch (character) {
            case ' ':
                this.isBlank = true;
                return;
            case '-':
                this.isPause = true;
                return;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                this.digit = (character - '0');
                return;
            default:
                throw new IllegalArgumentException("Can't render " + character);
            }
        }

        void dot() {
            this.isDotted = true;
        }

        Integer value() {
            if (isBlank) {
                return null;
            } else if (isPause) {
                return -1;
            } else {
                return digit;
            }
            
        }
    }
}
