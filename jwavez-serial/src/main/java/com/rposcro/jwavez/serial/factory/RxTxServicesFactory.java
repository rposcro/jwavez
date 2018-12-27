package com.rposcro.jwavez.serial.factory;

import com.rposcro.jwavez.serial.rxtx.SerialReceiver;
import com.rposcro.jwavez.serial.rxtx.SerialTransmitter;
import java.io.InputStream;
import java.io.OutputStream;

class RxTxServicesFactory {

  SerialReceiver createSerialReceiver(InputStream inputStream) {
    return new SerialReceiver(inputStream);
  }

  SerialTransmitter createSerialTransmitter(OutputStream outputStream) {
    return new SerialTransmitter(outputStream);
  }
}
