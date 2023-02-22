package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.ENABLE_SUC;
import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_SUC_NODE_ID;
import static com.rposcro.jwavez.serial.enums.SerialCommand.SEND_SUC_ID;
import static com.rposcro.jwavez.serial.enums.SerialCommand.SET_SUC_NODE_ID;
import static com.rposcro.jwavez.serial.utils.FieldUtil.booleanByte;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.model.TransmitOption;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class SucRequestBuilder extends AbstractRequestBuilder {

    public SerialRequest createEnableSUCRequest() {
        return nonPayloadRequest(ENABLE_SUC);
    }

    public SerialRequest createSetRemoteSUCNodeRequest(NodeId remoteNodeId, boolean enableSucAndSis, byte callbackFlowId) {
        return SerialRequest.builder()
                .responseExpected(true)
                .serialCommand(SET_SUC_NODE_ID)
                .frameData(startUpFrameBuffer(FRAME_CONTROL_SIZE + 5, SET_SUC_NODE_ID)
                        .put(remoteNodeId.getId())
                        .put(booleanByte(enableSucAndSis))
                        .put(booleanByte(true))
                        .put(booleanByte(enableSucAndSis))
                        .put(callbackFlowId)
                        .putCRC())
                .callbackFlowId(callbackFlowId)
                .build();
    }

    public SerialRequest createSetLocalSUCNodeRequest(NodeId localNodeId, boolean enableSucAndSis) {
        return SerialRequest.builder()
                .responseExpected(true)
                .serialCommand(SET_SUC_NODE_ID)
                .frameData(startUpFrameBuffer(FRAME_CONTROL_SIZE + 5, SET_SUC_NODE_ID)
                        .put(localNodeId.getId())
                        .put(booleanByte(enableSucAndSis))
                        .put(booleanByte(true))
                        .put(booleanByte(enableSucAndSis))
                        .put((byte) 0x00)
                        .putCRC())
                .build();
    }

    public SerialRequest createSendSUCIdRequest(NodeId addresseeId, byte callbackFlowId) {
        return SerialRequest.builder()
                .serialCommand(SEND_SUC_ID)
                .responseExpected(true)
                .frameData(startUpFrameBuffer(FRAME_CONTROL_SIZE + 3, SEND_SUC_ID)
                        .put(addresseeId.getId())
                        .put((byte) (TransmitOption.TRANSMIT_OPTION_ACK.getCode() | TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE.getCode()))
                        .put(callbackFlowId)
                        .putCRC())
                .callbackFlowId(callbackFlowId)
                .build();
    }

    public SerialRequest createGetSUCNodeIdRequest() {
        return nonPayloadRequest(GET_SUC_NODE_ID);
    }
}
