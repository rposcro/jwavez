package com.rposcro.jwavez.serial.exceptions;

public class RequestFlowException extends SerialStreamException {

  public RequestFlowException(String message) {
    super(message);
  }

  public RequestFlowException(String message, Object... replacements) {
    super(String.format(message, replacements));
  }

  public RequestFlowException(Throwable throwable) {
    super(throwable);
  }

  public RequestFlowException(Throwable throwable, String message) {
    super(throwable, message);
  }
}
