package com.rposcro.jwavez.serial.controllers;

import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import com.rposcro.jwavez.serial.handlers.LastResponseHolder;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.RxTxRouter;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.rxtx.port.NeuronRoboticsSerialPort;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Referring to scenario flow models, this controller applies to simple request-response flow model.
 * Any callbacks received while running are simply ignored.
 *
 * <B>Note!</B> This controller is not thread safe, must not send multiple requests at a same time.
 * This controller runs in context of current thread, all calls are synchronized and blocking.
 * Dedicated typical usages are flow scenarios with well defined end conditions, in other words
 * 'do the job and exit'.</br>
 */
@Slf4j
public class SimpleResponseController implements AutoCloseable {

  private String device;
  private RxTxRouter rxTxRouter;
  private SerialPort serialPort;
  private RxTxConfiguration configuration;

  private LastResponseHolder lastResponseHolder;

  @Builder
  public SimpleResponseController(@NonNull String device, RxTxConfiguration configuration) {
    this.configuration = configuration != null ? configuration : RxTxConfiguration.builder().build();
    this.lastResponseHolder = new LastResponseHolder();
    this.device = device;
    this.serialPort = new NeuronRoboticsSerialPort();
    this.rxTxRouter = RxTxRouter.builder()
        .configuration(configuration)
        .serialPort(serialPort)
        .responseHandler(this.lastResponseHolder)
        .build();
  }

  public SimpleResponseController connect() throws SerialPortException {
    this.serialPort.connect(device);
    return this;
  }

  @Override
  public void close() throws SerialPortException {
    this.serialPort.disconnect();
  }

  public <T extends ZWaveResponse> T requestResponseFlow(SerialRequest request) throws FlowException {
    try {
      rxTxRouter.runUnlessRequestSent(request);
      if (request.isResponseExpected()) {
        return (T) lastResponseHolder.get();
      } else {
        return null;
      }
    } catch(SerialException e) {
      log.error("Failed to execute request-response flow!", e);
      throw new FlowException(e);
    }
  }
}
