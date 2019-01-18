package com.rposcro.jwavez.tools.cli.exceptions;

public class CommandOptionsException extends CommandException {

  public CommandOptionsException(String message) {
    super(message);
  }

  public CommandOptionsException(String message, Object... messageReplacements) {
    super(String.format(message, messageReplacements));
  }

  public CommandOptionsException(Throwable throwable) {
    super(throwable);
  }

  public CommandOptionsException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public CommandOptionsException(Throwable throwable, String message, Object... messageReplacements) {
    super(String.format(message, messageReplacements), throwable);
  }
}
