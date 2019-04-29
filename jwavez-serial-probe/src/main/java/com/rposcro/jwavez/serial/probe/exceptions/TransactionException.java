package com.rposcro.jwavez.serial.probe.exceptions;

public class TransactionException extends SerialException {

  public TransactionException(String message) {
    super(message);
  }

  public TransactionException(Exception e) {
    super(e);
  }

  public TransactionException(String message, Exception e) {
    super(message, e);
  }
}
