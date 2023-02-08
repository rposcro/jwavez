package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_SUC_NODE_ID;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class GetSUCNodeIdRequest extends ZWaveRequest {

    public static SerialRequest createGetSUCNodeIdRequest() {
        return nonPayloadRequest(GET_SUC_NODE_ID);
    }
}
