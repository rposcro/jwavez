package com.rposcro.jwavez.serial.probe.factory;

import com.rposcro.jwavez.serial.probe.rxtx.SerialReceiver;
import com.rposcro.jwavez.serial.probe.rxtx.SerialTransmitter;
import java.io.InputStream;
import java.io.OutputStream;

public class RxTxServicesFactory {

  public SerialReceiver createSerialReceiver(InputStream inputStream) {
    return new SerialReceiver(inputStream);
  }

  public SerialTransmitter createSerialTransmitter(OutputStream outputStream) {
    return new SerialTransmitter(outputStream);
  }
}
