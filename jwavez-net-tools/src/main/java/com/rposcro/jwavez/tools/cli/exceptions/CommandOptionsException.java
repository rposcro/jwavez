package com.rposcro.jwavez.tools.cli.exceptions;

public class CommandOptionsException extends CommandException {

  public CommandOptionsException(String message) {
    super(message);
  }

  public CommandOptionsException(Throwable throwable) {
    super(throwable);
  }

  public CommandOptionsException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
