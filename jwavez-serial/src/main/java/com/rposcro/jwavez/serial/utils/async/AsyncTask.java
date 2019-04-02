package com.rposcro.jwavez.serial.utils.async;

import com.rposcro.jwavez.serial.exceptions.SerialException;

@FunctionalInterface
public interface AsyncTask<T> {

  public T execute() throws SerialException;
}
