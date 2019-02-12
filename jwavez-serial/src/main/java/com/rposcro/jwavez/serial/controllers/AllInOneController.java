package com.rposcro.jwavez.serial.controllers;

import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.FrameException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.exceptions.SerialStreamException;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import com.rposcro.jwavez.serial.handlers.LastResponseHolder;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.RxTxRouter;
import com.rposcro.jwavez.serial.rxtx.port.NeuronRoboticsSerialPort;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>Note!</B> This controller is not thread safe, must not send multiple requests at a same time.
 */
@Slf4j
public class AllInOneController implements AutoCloseable {

  private String device;
  private RxTxRouter rxTxRouter;
  private SerialPort serialPort;
  private Thread routerThread;

  private LastResponseHolder lastResponseHolder;

  @Builder
  public AllInOneController(@NonNull String device, RxTxConfiguration configuration) {
    if (configuration == null) {
      configuration = RxTxConfiguration.builder().build();
    }

    this.lastResponseHolder = new LastResponseHolder();
    this.device = device;
    this.serialPort = new NeuronRoboticsSerialPort();
    this.rxTxRouter = RxTxRouter.builder()
        .configuration(configuration)
        .serialPort(serialPort)
        .responseHandler(this.lastResponseHolder)
        .build();
  }

  public AllInOneController connect() throws SerialPortException {
    this.serialPort.connect(device);
    this.routerThread = new Thread(this.rxTxRouter);
    this.routerThread.setName("RxTxRouter to " + device);
    this.routerThread.setDaemon(true);
    this.routerThread.start();
    return this;
  }

  @Override
  public void close() throws SerialPortException {
    this.routerThread.interrupt();
    this.serialPort.disconnect();
  }

  public <T extends ZWaveResponse> T requestResponseFlow(SerialRequest request) throws FlowException {
    try {
      rxTxRouter.sendFrame(request);
      ZWaveResponse response = lastResponseHolder.get();
      return (T) response;
    } catch(SerialStreamException | FrameException e) {
      log.error("Failed to execute request-response flow!", e);
      throw new FlowException(e);
    }
  }
}
