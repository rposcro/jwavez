package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.REMOVE_NODE_FROM_NETWORK;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkMode;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class RemoveNodeFromNetworkRequestBuilder extends AbstractRequestBuilder {

    private static final int REMOVE_NETWORK_WIDE_OPTION = 0x40;

    public RemoveNodeFromNetworkRequestBuilder(ByteBufferManager byteBufferManager) {
        super(byteBufferManager);
    }

    public SerialRequest createRemoveNodeFromNetworkRequest(RemoveNodeFromNeworkMode mode, byte callbackFlowId, boolean networkWide) {
        ImmutableBuffer buffer = dataBuilder(REMOVE_NODE_FROM_NETWORK, 2)
                .add((byte) (mode.getCode() | (networkWide ? REMOVE_NETWORK_WIDE_OPTION : 0x00)))
                .add(callbackFlowId)
                .build();
        return SerialRequest.builder()
                .responseExpected(false)
                .frameData(buffer)
                .serialCommand(REMOVE_NODE_FROM_NETWORK)
                .callbackFlowId(callbackFlowId)
                .build();
    }

    public SerialRequest createStartRemoveAnyNodeRequest(byte callbackFlowId) {
        return createRemoveNodeFromNetworkRequest(RemoveNodeFromNeworkMode.REMOVE_NODE_ANY, callbackFlowId, true);
    }

    public SerialRequest createStopTransactionRequest(byte callbackFlowId) {
        return createRemoveNodeFromNetworkRequest(RemoveNodeFromNeworkMode.REMOVE_NODE_STOP, callbackFlowId, true);
    }

    public SerialRequest createFinalTransactionRequest() {
        return createRemoveNodeFromNetworkRequest(RemoveNodeFromNeworkMode.REMOVE_NODE_STOP, (byte) 0, true);
    }
}
