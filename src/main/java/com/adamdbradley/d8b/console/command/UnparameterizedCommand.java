package com.adamdbradley.d8b.console.command;

/**
 * Base class for commands that require no parameters.
 */
abstract class UnparameterizedCommand extends Command {

    public UnparameterizedCommand(final CommandType commandType) {
        super(commandType);
    }

    @Override
    public final String serializeParameters() {
        return "";
    }

}
