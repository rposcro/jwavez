package com.rposcro.jwavez.serial.controllers.helpers;

import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.frames.FramesModelRegistry;
import com.rposcro.jwavez.serial.frames.callbacks.FlowCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.responses.SolicitedCallbackResponse;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;

public class RequestCallbackFlowHelper {

  private static RequestCallbackFlowHelper helperInstance;

  private FramesModelRegistry framesModelRegistry;

  public RequestCallbackFlowHelper() {
    this.framesModelRegistry = FramesModelRegistry.defaultRegistry();
  }

  public static RequestCallbackFlowHelper defaultHelper() {
    return helperInstance == null ? helperInstance = new RequestCallbackFlowHelper() : helperInstance;
  }

  public <T extends SolicitedCallbackResponse> Class<T> solicitedCallbackResponseClass(SerialCommand command) throws FlowException {
    Class<? extends ZWaveResponse> responseClass = FramesModelRegistry.defaultRegistry().responseClass(command.getCode())
        .orElseThrow(() -> new FlowException("No correlated response class found for serial command: %s", command));
    if (!SolicitedCallbackResponse.class.isAssignableFrom(responseClass)) {
      throw new FlowException("Correlated response class %s is not of type %s", responseClass, SolicitedCallbackResponse.class);
    }
    return (Class<T>) responseClass;
  }

  public <T extends FlowCallback> Class<T> solicitedCallbackClass(SerialCommand command) throws FlowException {
    Class<? extends ZWaveCallback> callbackClass = FramesModelRegistry.defaultRegistry().callbackClass(command.getCode())
        .orElseThrow(() -> new FlowException("No correlated function callback class found for serial command: %s", command));
    if (!FlowCallback.class.isAssignableFrom(callbackClass)) {
      throw new FlowException("Correlated callback class %s is not of type %s", callbackClass, FlowCallback.class);
    }
    return (Class<T>) callbackClass;
  }
}
