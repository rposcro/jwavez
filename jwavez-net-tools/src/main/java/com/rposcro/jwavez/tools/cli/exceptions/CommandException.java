package com.rposcro.jwavez.tools.cli.exceptions;

public class CommandException extends Exception {

  public CommandException(String message) {
    super(message);
  }

  public CommandException(Throwable throwable) {
    super(throwable);
  }
}
