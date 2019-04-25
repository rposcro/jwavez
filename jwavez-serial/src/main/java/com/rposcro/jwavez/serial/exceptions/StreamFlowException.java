package com.rposcro.jwavez.serial.exceptions;

public class StreamFlowException extends StreamException {

  public StreamFlowException(String message) {
    super(message);
  }

  public StreamFlowException(String message, Object... replacements) {
    super(String.format(message, replacements));
  }

  public StreamFlowException(Throwable throwable) {
    super(throwable);
  }

  public StreamFlowException(Throwable throwable, String message) {
    super(throwable, message);
  }
}
