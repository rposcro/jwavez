package com.rposcro.jwavez.tools.cli.exceptions;

public class CommandLineException extends CommandException {

  public CommandLineException(String message) {
    super(message);
  }

  public CommandLineException(Throwable throwable) {
    super(throwable);
  }
}
