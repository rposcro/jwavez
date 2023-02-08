package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.ENABLE_SUC;
import static com.rposcro.jwavez.serial.enums.SerialCommand.MEMORY_GET_ID;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class EnableSUCRequest extends ZWaveRequest {

    public static SerialRequest createEnableSUCRequest() {
        return nonPayloadRequest(ENABLE_SUC);
    }
}
