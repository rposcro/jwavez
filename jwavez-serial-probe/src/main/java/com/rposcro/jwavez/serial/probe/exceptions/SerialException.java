package com.rposcro.jwavez.serial.probe.exceptions;

public class SerialException extends RuntimeException {

  public SerialException(String message) {
    super(message);
  }

  public SerialException(Throwable t) {
    super(t);
  }

  public SerialException(String message, Throwable t) {
    super(message, t);
  }
}
