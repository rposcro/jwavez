package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SET_SUC_NODE_ID;
import static com.rposcro.jwavez.serial.utils.FieldUtil.booleanByte;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class SetSUCNodeIdRequest extends ZWaveRequest {

  public static SerialRequest createSetRemoteSUCNodeRequest(NodeId remoteNodeId, boolean enableSucAndSis, byte callbackFlowId) {
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

  public static SerialRequest createSetLocalSUCNodeRequest(NodeId localNodeId, boolean enableSucAndSis) {
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
}
