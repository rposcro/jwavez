package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.exceptions.SerialStreamException;
import gnu.io.NRSerialPort;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class NeuronRoboticsSerialConnection implements SerialConnection {

  private static final int DEFAULT_BAUD_RATE = 115200;

  private NRSerialPort port;
  private ReadableByteChannel inputChannel;
  private WritableByteChannel outputChannel;

  @Override
  public void connect(String device) throws SerialStreamException {
    try {
      this.port = new NRSerialPort(device, DEFAULT_BAUD_RATE);
      this.port.connect();
      this.inputChannel = Channels.newChannel(port.getInputStream());
      this.outputChannel = Channels.newChannel(port.getOutputStream());
    } catch(Exception e) {
      throw new SerialStreamException(e);
    }
  }

  @Override
  public int readData(ByteBuffer buffer) throws IOException {
    return inputChannel.read(buffer);
  }

  @Override
  public int writeData(ByteBuffer buffer) throws IOException {
    return outputChannel.write(buffer);
  }
}
