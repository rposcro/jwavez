package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.IS_FAILED_NODE_ID;

public class FailedNodeRequestBuilder extends AbstractRequestBuilder {

    public FailedNodeRequestBuilder(ByteBufferManager byteBufferManager) {
        super(byteBufferManager);
    }

    public SerialRequest createIsFailedNodeRequest(NodeId nodeId) {
        ImmutableBuffer buffer = dataBuilder(IS_FAILED_NODE_ID, 1)
            .add(nodeId.getId())
            .build();
        return SerialRequest.builder()
            .frameData(buffer)
            .responseExpected(true)
            .serialCommand(IS_FAILED_NODE_ID)
            .build();
    }
}
