package com.rposcro.jwavez.serial.rxtx.port;

import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import gnu.io.NRSerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class NeuronRoboticsSerialPort implements SerialPort {

  private static final int DEFAULT_BAUD_RATE = 115200;

  private String device;
  private NRSerialPort port;
  private InputStream inputStream;
  private ReadableByteChannel inputChannel;
  private WritableByteChannel outputChannel;

  @Override
  public synchronized void connect(String device) throws SerialPortException {
    try {
      if (this.port != null) {
        throw new SerialPortException("Port is already connected with device %s!", device);
      }

      this.device = device;
      this.port = new NRSerialPort(device, DEFAULT_BAUD_RATE);
      this.port.connect();
      this.inputStream = port.getInputStream();
      this.inputChannel = Channels.newChannel(inputStream);
      this.outputChannel = Channels.newChannel(port.getOutputStream());
    } catch(Exception e) {
      throw new SerialPortException(e, "Could not connect with device %s!", device);
    }
  }

  @Override
  public synchronized void reconnect() throws SerialPortException {
    disconnect();
    connect(device);
  }

  @Override
  public synchronized void disconnect() throws SerialPortException {
    try {
      port.disconnect();
      port = null;
    } catch(Exception e) {
      throw new SerialPortException(e, "Could not successfully disconnect from serial channel!");
    }
  }

  @Override
  public int readData(ByteBuffer buffer) throws SerialPortException {
    try {
      if (inputStream.available() > 0) {
        return inputChannel.read(buffer);
      } else {
        return 0;
      }
    } catch(IOException e) {
      throw new SerialPortException(e, "Exception occurred while reading from channel!");
    }
  }

  @Override
  public int writeData(ByteBuffer buffer) throws SerialPortException {
    try {
      return outputChannel.write(buffer);
    } catch(IOException e) {
      throw new SerialPortException(e, "Exception occurred while writing to channel!");
    }
  }
}
