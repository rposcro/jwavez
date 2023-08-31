package com.rposcro.jwavez.tools.cli.exceptions;

public class CommandExecutionException extends CommandException {

    public CommandExecutionException(String message) {
        super(message);
    }

    public CommandExecutionException(Throwable throwable) {
        super(throwable);
    }

    public CommandExecutionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
