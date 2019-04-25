package com.rposcro.jwavez.serial.controllers;

import static com.rposcro.jwavez.core.utils.ObjectsUtil.orDefault;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.controllers.helpers.RequestCallbackFlowHelper;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.FrameException;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.exceptions.RxTxException;
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
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Referring to scenario flow models, this controller applies to simple request-response or request-response-callback
 * flows. Any not solicited callbacks are simply ignored. This controller does not apply to complex flow scenarios
 * like nodes inclusion or exclusion.
 *
 * <B>Note!</B> This controller is not thread safe, must not send multiple requests at a time.
 * This controller runs in context of current thread, all calls are synchronized and blocking.
 * Dedicated typical usages are flow scenarios with well defined end conditions, in other words
 * 'do the job and exit'.</br>
 */
@Slf4j
public class BasicSynchronousController extends AbstractClosableController<BasicSynchronousController> {

  private static final long DEFAULT_CALLBACK_TIMEOUT_MILLIS = 5000;

  private RxTxRouter rxTxRouter;
  private InboundFrameParser parser;
  private InboundFrameValidator validator;
  private RequestCallbackFlowHelper flowHelper;

  private LastResponseHolder lastResponseHolder;
  private ZWaveCallback lastMatchingCallback;
  private Optional<Class<? extends ZWaveCallback>> expectedCallbackClass;

  @Builder
  public BasicSynchronousController(@NonNull String dongleDevice, RxTxConfiguration rxTxConfiguration) {
    this.rxTxConfiguration = orDefault(rxTxConfiguration, RxTxConfiguration::defaultConfiguration);
    this.lastResponseHolder = new LastResponseHolder();
    this.dongleDevice = dongleDevice;
    this.serialPort = new NeuronRoboticsSerialPort();
    this.rxTxRouter = RxTxRouter.builder()
        .configuration(this.rxTxConfiguration)
        .serialPort(serialPort)
        .responseHandler(this.lastResponseHolder)
        .callbackHandler(this::handleCallback)
        .build();
    this.validator = InboundFrameValidator.defaultValidator();
    this.parser = InboundFrameParser.defaultParser();
    this.flowHelper = RequestCallbackFlowHelper.defaultHelper();
  }

  public <T extends ZWaveResponse> T requestResponseFlow(SerialRequest request) throws RxTxException, FrameException {
    return runRequest(request);
  }

  public <T extends ZWaveCallback> T requestCallbackFlow(SerialRequest request) throws FlowException, RxTxException, FrameException {
    return requestCallbackFlow(request, DEFAULT_CALLBACK_TIMEOUT_MILLIS);
  }

  public <T extends ZWaveCallback> T requestCallbackFlow(SerialRequest request, long timeout) throws FlowException, RxTxException, FrameException {
    try {
      lastMatchingCallback = null;
      flowHelper.solicitedCallbackResponseClass(request.getSerialCommand());
      expectedCallbackClass = Optional.of(flowHelper.solicitedCallbackClass(request.getSerialCommand()));

      SolicitedCallbackResponse response = runRequest(request);
      if (request.isResponseExpected() && !response.isSolicitedCallbackToFollow()) {
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
    } finally {
      expectedCallbackClass = Optional.empty();
      lastMatchingCallback = null;
    }
  }

  private <T extends ZWaveResponse> T runRequest(SerialRequest request) throws RxTxException, FrameException {
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
