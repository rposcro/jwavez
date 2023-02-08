package com.rposcro.jwavez.serial.services.requests;

import com.rposcro.jwavez.serial.buffers.dispatchers.BufferDispatcher;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import lombok.Builder;

public class ControllerInformationRequests extends AbstractFrameRequests {

    @Builder
    public ControllerInformationRequests(BufferDispatcher bufferDispatcher) {
        super(bufferDispatcher);
    }

    public SerialRequest getCapabilitiesRequest() {
        return commandRequest(SerialCommand.GET_CAPABILITIES, true);
    }

    public SerialRequest getControllerCapabilitiesRequest() {
        return commandRequest(SerialCommand.GET_CONTROLLER_CAPABILITIES, true);
    }

    public SerialRequest getLibraryTypeRequest() {
        return commandRequest(SerialCommand.GET_LIBRARY_TYPE, true);
    }

    public SerialRequest getVersionRequest() {
        return commandRequest(SerialCommand.GET_VERSION, true);
    }

    public SerialRequest memoryGetIdRequest() {
        return commandRequest(SerialCommand.MEMORY_GET_ID, true);
    }

    public SerialRequest getInitDataRequest() {
        return commandRequest(SerialCommand.GET_INIT_DATA, true);
    }

    public SerialRequest getRFPowerLevelRequest() {
        return commandRequest(SerialCommand.RF_POWER_LEVEL_GET, true);
    }
}
