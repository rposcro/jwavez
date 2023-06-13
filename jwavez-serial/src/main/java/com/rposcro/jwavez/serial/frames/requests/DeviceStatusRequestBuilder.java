package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_NETWORK_STATS;
import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_PROTOCOL_STATUS;
import static com.rposcro.jwavez.serial.enums.SerialCommand.RF_POWER_LEVEL_GET;

public class DeviceStatusRequestBuilder extends AbstractRequestBuilder {

    public DeviceStatusRequestBuilder(ByteBufferManager byteBufferManager) {
        super(byteBufferManager);
    }

    public SerialRequest createGetNetworkStatsRequest() {
        return nonPayloadRequest(GET_NETWORK_STATS);
    }

    public SerialRequest createGetProtocolStatusRequest() {
        return nonPayloadRequest(GET_PROTOCOL_STATUS);
    }

    public SerialRequest createGetRFPowerLevelRequest() {
        return nonPayloadRequest(RF_POWER_LEVEL_GET);
    }
}
