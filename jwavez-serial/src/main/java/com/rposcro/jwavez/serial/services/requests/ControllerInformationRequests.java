package com.rposcro.jwavez.serial.services.requests;

import com.rposcro.jwavez.serial.buffers.dispatchers.BufferDispatcher;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.FrameRequest;
import lombok.Builder;

public class ControllerInformationRequests extends AbstractFrameRequests {

  @Builder
  public ControllerInformationRequests(BufferDispatcher bufferDispatcher) {
    super(bufferDispatcher);
  }

  public FrameRequest getCapabilitiesRequest() {
    return commandRequest(SerialCommand.GET_CAPABILITIES, true);
  }

  public FrameRequest getControllerCapabilitiesRequest() {
    return commandRequest(SerialCommand.GET_CONTROLLER_CAPABILITIES, true);
  }

  public FrameRequest getLibraryTypeRequest() {
    return commandRequest(SerialCommand.GET_LIBRARY_TYPE, true);
  }

  public FrameRequest getVersionRequest() {
    return commandRequest(SerialCommand.GET_VERSION, true);
  }

  public FrameRequest memoryGetIdRequest() {
    return commandRequest(SerialCommand.MEMORY_GET_ID, true);
  }

  public FrameRequest getInitDataRequest() {
    return commandRequest(SerialCommand.GET_INIT_DATA, true);
  }

  public FrameRequest getRFPowerLevelRequest() {
    return commandRequest(SerialCommand.RF_POWER_LEVEL_GET, true);
  }
}
