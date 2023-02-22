package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkMode;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.ADD_NODE_TO_NETWORK;

public class AddNodeToNetworkRequestBuilder extends AbstractRequestBuilder {

    public SerialRequest createAddNodeToNetworkRequest(AddNodeToNeworkMode mode, byte callbackFlowId, boolean networkWide, boolean normalPower) {
        FrameBuffer buffer = startUpFrameBuffer(FRAME_CONTROL_SIZE + 2, ADD_NODE_TO_NETWORK)
                .put((byte) (mode.getCode() | (networkWide ? 0x40 : 0x00) | (normalPower ? 0x80 : 0x00)))
                .put(callbackFlowId)
                .putCRC();
        return SerialRequest.builder()
                .responseExpected(false)
                .frameData(buffer)
                .serialCommand(ADD_NODE_TO_NETWORK)
                .callbackFlowId(callbackFlowId)
                .build();
    }

    public SerialRequest createStartAddAnyNodeRequest(byte callbackFlowId) {
        return createAddNodeToNetworkRequest(AddNodeToNeworkMode.ADD_NODE_ANY, callbackFlowId, true, true);
    }

    public SerialRequest createStopTransactionRequest(byte callbackFlowId) {
        return createAddNodeToNetworkRequest(AddNodeToNeworkMode.ADD_NODE_STOP, callbackFlowId, true, true);
    }

    public SerialRequest createFinalTransactionRequest() {
        return createAddNodeToNetworkRequest(AddNodeToNeworkMode.ADD_NODE_STOP, (byte) 0, true, true);
    }
}
