package com.rposcro.jwavez.serial.controllers;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.FramesModelRegistry;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.InboundFrameValidator;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.responses.SolicitedCallbackResponse;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import com.rposcro.jwavez.serial.handlers.LastResponseHolder;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.RxTxRouter;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.rxtx.port.NeuronRoboticsSerialPort;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Referring to scenario flow models, the controller applies to simple request-response-callback flow model.
 * Any not solicited callbacks are simply ignored. This controller does not apply to complex flow scenarios
 * like nodes inclusion or exclusion.
 *
 * <B>Note!</B> This controller is not thread safe, must not send multiple requests at a same time.
 * This controller runs in context of current thread, all calls are synchronized and blocking.
 * Dedicated typical usages are flow scenarios with well defined end conditions, in other words
 * 'do the job and exit'.</br>
 */
@Slf4j
public class SolicitedCallbackController implements AutoCloseable {

  private static final long DEFAULT_CALLBACK_TIMEOUT_MILLIS = 5000;

  private String device;
  private RxTxRouter rxTxRouter;
  private SerialPort serialPort;
  private RxTxConfiguration configuration;
  private InboundFrameParser parser;
  private InboundFrameValidator validator;

  private LastResponseHolder lastResponseHolder;
  private ZWaveCallback lastMatchingCallback;
  private Optional<Class<? extends ZWaveCallback>> expectedCallbackClass;

  @Builder
  public SolicitedCallbackController(@NonNull String device, RxTxConfiguration configuration) {
    this.configuration = configuration != null ? configuration : RxTxConfiguration.builder().build();
    this.lastResponseHolder = new LastResponseHolder();
    this.device = device;
    this.serialPort = new NeuronRoboticsSerialPort();
    this.rxTxRouter = RxTxRouter.builder()
        .configuration(configuration)
        .serialPort(serialPort)
        .responseHandler(this.lastResponseHolder)
        .callbackHandler(this::handleCallback)
        .build();
    this.validator = new InboundFrameValidator();
    this.parser = new InboundFrameParser();
  }

  public SolicitedCallbackController connect() throws SerialPortException {
    this.serialPort.connect(device);
    return this;
  }

  @Override
  public void close() throws SerialPortException {
    this.serialPort.disconnect();
  }

  public <T extends ZWaveCallback> T requestCallbackFlow(SerialRequest request) throws  FlowException {
    return requestCallbackFlow(request, DEFAULT_CALLBACK_TIMEOUT_MILLIS);
  }

  public <T extends ZWaveCallback> T requestCallbackFlow(SerialRequest request, long timeout) throws  FlowException {
    try {
      lastMatchingCallback = null;
      responseClass(request.getSerialCommand());
      expectedCallbackClass = Optional.of(callbackClass(request.getSerialCommand()));

      SolicitedCallbackResponse response = runRequest(request);
      if (!response.isSolicitedCallbackToFollow()) {
        throw new FlowException("Received response is not expecting callback to follow!");
      }

      long timeoutPoint = System.currentTimeMillis() + timeout;

      while(true) {
        rxTxRouter.runSingleCycle();
        if (lastMatchingCallback != null) {
          return (T) lastMatchingCallback;
        } else {
          if (timeoutPoint < System.currentTimeMillis()) {
            throw new FlowException("Callback timeout!");
          }
        }
      }
    } catch(SerialException e) {
      log.error("Failed to execute request-response-callback flow!", e);
      throw new FlowException(e);
    } finally {
      expectedCallbackClass = Optional.empty();
      lastMatchingCallback = null;
    }
  }

  private <T extends SolicitedCallbackResponse> Class<T> responseClass(SerialCommand command) throws FlowException {
    Class<? extends ZWaveResponse> responseClass = FramesModelRegistry.defaultRegistry().responseClass(command.getCode())
        .orElseThrow(() -> new FlowException("No correlated response class found for serial command: %s", command));
    if (!SolicitedCallbackResponse.class.isAssignableFrom(responseClass)) {
      throw new FlowException("Correlated response class %s is not of type %s", responseClass, SolicitedCallbackResponse.class);
    }
    return (Class<T>) responseClass;
  }

  private <T extends ZWaveCallback> Class<T> callbackClass(SerialCommand command) throws FlowException {
    Class<? extends ZWaveCallback> callbackClass = FramesModelRegistry.defaultRegistry().callbackClass(command.getCode())
        .orElseThrow(() -> new FlowException("No correlated callback class found for serial command: %s", command));
    return (Class<T>) callbackClass;
  }

  private <T extends SolicitedCallbackResponse> T runRequest(SerialRequest request) throws SerialException {
    rxTxRouter.runUnlessRequestSent(request);
    if (request.isResponseExpected()) {
      return (T) lastResponseHolder.get();
    } else {
      return null;
    }
  }

  private void handleCallback(ViewBuffer frameBuffer) {
    expectedCallbackClass.ifPresent(clazz -> {
      if (validator.validate(frameBuffer)) {
        try {
          ZWaveCallback callback = parser.parseCallbackFrame(frameBuffer);
          if (clazz.isAssignableFrom(callback.getClass())) {
            this.lastMatchingCallback = callback;
          } else {
            log.info("Received callback but not of expected class {}", clazz);
          }
        } catch (FrameParseException e) {
          log.warn("Callback frame parsing failed!");
        }
      } else {
        log.warn("Callback frame validation failed!");
      }
    });
  }
}
