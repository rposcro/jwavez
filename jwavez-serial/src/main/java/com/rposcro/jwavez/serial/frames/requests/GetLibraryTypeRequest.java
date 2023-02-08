package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_LIBRARY_TYPE;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class GetLibraryTypeRequest extends ZWaveRequest {

    public static SerialRequest createLibraryTypeRequest() {
        return nonPayloadRequest(GET_LIBRARY_TYPE);
    }
}
