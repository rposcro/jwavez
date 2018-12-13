package com.rposcro.jwavez.serial;

import com.rposcro.jwavez.serial.configuration.FramesConfiguration;
import com.rposcro.jwavez.serial.configuration.RxTxConfiguration;
import com.rposcro.jwavez.serial.configuration.TransactionConfiguration;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import gnu.io.NRSerialPort;

public class SerialManager {

  private static final int BAUD_RATE = 115200;

  private String device;
  private NRSerialPort port;
  private SerialChannel serialChannel;

  public SerialManager(String device) {
    this.device = device;
  }

  public SerialChannel connect() {
    if (isReady()) {
      throw new IllegalStateException("Already connected!");
    }

    this.port = new NRSerialPort(device, BAUD_RATE);
    this.port.connect();
    this.serialChannel = buildSerialChannel(port);
    return this.serialChannel;
  }

  public SerialChannel channel() {
    if (!isReady()) {
      throw new SerialException("Channel hasn't been connected yet!");
    }
    return this.serialChannel;
  }

  public boolean isReady() {
    return port != null;
  }


  private SerialChannel buildSerialChannel(NRSerialPort port) {
    FramesConfiguration framesConfiguration = new FramesConfiguration();
    RxTxConfiguration rxTxConfiguration = new RxTxConfiguration(port, framesConfiguration);
    TransactionConfiguration transactionConfiguration = new TransactionConfiguration(rxTxConfiguration);
    SerialChannel channel = new SerialChannel(transactionConfiguration.getTransactionManager(), rxTxConfiguration.getInboundFrameProcessor());
    rxTxConfiguration.activate();
    return channel;
  }
}
