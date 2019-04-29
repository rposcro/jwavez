package com.rposcro.jwavez.serial.controllers;

import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;

public abstract class AbstractClosableController<T extends AbstractClosableController> implements AutoCloseable {

  protected String dongleDevice;
  protected SerialPort serialPort;
  protected RxTxConfiguration rxTxConfiguration;

  public T connect() throws SerialPortException {
    this.serialPort.connect(dongleDevice);
    return (T) this;
  }

  @Override
  public void close() throws SerialPortException {
    this.serialPort.disconnect();
  }
}
