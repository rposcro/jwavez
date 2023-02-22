package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_CAPABILITIES;
import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_CONTROLLER_CAPABILITIES;
import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_INIT_DATA;
import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_LIBRARY_TYPE;
import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_VERSION;
import static com.rposcro.jwavez.serial.enums.SerialCommand.MEMORY_GET_ID;

public class DongleFactsRequestBuilder extends AbstractRequestBuilder {

    public SerialRequest createGetInitDataRequest() {
        return nonPayloadRequest(GET_INIT_DATA);
    }

    public SerialRequest createGetControllerCapabilitiesRequest() {
        return nonPayloadRequest(GET_CONTROLLER_CAPABILITIES);
    }

    public SerialRequest createGetCapabilitiesRequest() {
        return nonPayloadRequest(GET_CAPABILITIES);
    }

    public SerialRequest createGetLibraryTypeRequest() {
        return nonPayloadRequest(GET_LIBRARY_TYPE);
    }

    public SerialRequest createGetVersionRequest() {
        return nonPayloadRequest(GET_VERSION);
    }

    public SerialRequest createMemoryGetIdRequest() {
        return nonPayloadRequest(MEMORY_GET_ID);
    }
}
