package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.ENABLE_SUC;
import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_SUC_NODE_ID;
import static com.rposcro.jwavez.serial.enums.SerialCommand.SEND_SUC_ID;
import static com.rposcro.jwavez.serial.enums.SerialCommand.SET_SUC_NODE_ID;
import static com.rposcro.jwavez.serial.utils.FieldUtil.booleanByte;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.model.TransmitOption;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class SucRequestBuilder extends AbstractRequestBuilder {

    public SucRequestBuilder(ByteBufferManager byteBufferManager) {
        super(byteBufferManager);
    }

    public SerialRequest createEnableSUCRequest() {
        return nonPayloadRequest(ENABLE_SUC);
    }

    public SerialRequest createSetRemoteSUCNodeRequest(NodeId remoteNodeId, boolean enableSucAndSis, byte callbackFlowId) {
        ImmutableBuffer buffer = dataBuilder(SET_SUC_NODE_ID, 5)
                .add(remoteNodeId.getId())
                .add(booleanByte(enableSucAndSis))
                .add(booleanByte(true))
                .add(booleanByte(enableSucAndSis))
                .add(callbackFlowId)
                .build();
        return SerialRequest.builder()
                .responseExpected(true)
                .serialCommand(SET_SUC_NODE_ID)
                .frameData(buffer)
                .callbackFlowId(callbackFlowId)
                .build();
    }

    public SerialRequest createSetLocalSUCNodeRequest(NodeId localNodeId, boolean enableSucAndSis) {
        ImmutableBuffer buffer = dataBuilder(SET_SUC_NODE_ID, 5)
                .add(localNodeId.getId())
                .add(booleanByte(enableSucAndSis))
                .add(booleanByte(true))
                .add(booleanByte(enableSucAndSis))
                .add((byte) 0x00)
                .build();
        return SerialRequest.builder()
                .responseExpected(true)
                .serialCommand(SET_SUC_NODE_ID)
                .frameData(buffer)
                .build();
    }

    public SerialRequest createSendSUCIdRequest(NodeId addresseeId, byte callbackFlowId) {
        ImmutableBuffer buffer = dataBuilder(SEND_SUC_ID, 3)
                .add(addresseeId.getId())
                .add((byte) (TransmitOption.TRANSMIT_OPTION_ACK.getCode() | TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE.getCode()))
                .add(callbackFlowId)
                .build();
        return SerialRequest.builder()
                .serialCommand(SEND_SUC_ID)
                .responseExpected(true)
                .frameData(buffer)
                .callbackFlowId(callbackFlowId)
                .build();
    }

    public SerialRequest createGetSUCNodeIdRequest() {
        return nonPayloadRequest(GET_SUC_NODE_ID);
    }
}
