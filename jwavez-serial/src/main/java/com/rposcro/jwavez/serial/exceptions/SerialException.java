package com.rposcro.jwavez.serial.exceptions;

public class SerialException extends Exception {

  public SerialException(String message) {
    super(message);
  }

  public SerialException(Throwable throwable) {
    super(throwable);
  }

  public SerialException(Throwable throwable, String message) {
    super(message, throwable);
  }
}
