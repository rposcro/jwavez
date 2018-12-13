package com.rposcro.jwavez.serial.exceptions;

public class CommunicationTimeoutException extends CommunicationException {

  public CommunicationTimeoutException(String message) {
    super(message);
  }

  public CommunicationTimeoutException(Exception e) {
    super(e);
  }

  public CommunicationTimeoutException(String message, Exception e) {
    super(message, e);
  }
}
