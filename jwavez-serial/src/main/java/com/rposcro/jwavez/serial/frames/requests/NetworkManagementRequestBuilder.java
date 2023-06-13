package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.REQUEST_NODE_INFO;

public class NetworkManagementRequestBuilder extends AbstractRequestBuilder {

    public NetworkManagementRequestBuilder(ByteBufferManager byteBufferManager) {
        super(byteBufferManager);
    }

    public SerialRequest createRequestNodeInfoRequest(NodeId nodeId) {
        ImmutableBuffer buffer = dataBuilder(REQUEST_NODE_INFO, 1)
                .add(nodeId.getId())
                .build();
        return SerialRequest.builder()
                .frameData(buffer)
                .responseExpected(true)
                .serialCommand(REQUEST_NODE_INFO)
                .build();
    }
}
