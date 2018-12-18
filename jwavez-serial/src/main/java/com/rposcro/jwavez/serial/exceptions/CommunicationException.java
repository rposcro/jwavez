package com.rposcro.jwavez.serial.exceptions;

public class CommunicationException extends SerialException {

  public CommunicationException(String message) {
    super(message);
  }

  public CommunicationException(Exception e) {
    super(e);
  }

  public CommunicationException(String message, Exception e) {
    super(message, e);
  }
}
