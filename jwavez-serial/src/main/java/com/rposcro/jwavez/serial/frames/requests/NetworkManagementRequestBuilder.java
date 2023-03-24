package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.REQUEST_NODE_INFO;

public class NetworkManagementRequestBuilder extends AbstractRequestBuilder {

    public SerialRequest createRequestNodeInfoRequest(NodeId nodeId) {
        return SerialRequest.builder()
                .frameData(startUpFrameBuffer(FRAME_CONTROL_SIZE + 1, REQUEST_NODE_INFO)
                        .put(nodeId.getId())
                        .putCRC())
                .responseExpected(true)
                .serialCommand(REQUEST_NODE_INFO)
                .build();
    }
}
