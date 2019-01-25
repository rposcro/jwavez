package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.exceptions.SerialStreamException;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface SerialConnection {

  void connect(String device) throws SerialStreamException;

  int readData(ByteBuffer buffer) throws IOException;
  int writeData(ByteBuffer buffer) throws IOException;
}
