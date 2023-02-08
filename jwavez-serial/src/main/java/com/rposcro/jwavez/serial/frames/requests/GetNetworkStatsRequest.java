package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_NETWORK_STATS;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class GetNetworkStatsRequest extends ZWaveRequest {

    public static SerialRequest createGetNetworkStatsRequest() {
        return nonPayloadRequest(GET_NETWORK_STATS);
    }
}
