package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.REMOVE_NODE_FROM_NETWORK;

import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkMode;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class RemoveNodeFromNetworkRequest extends ZWaveRequest {

  private static final int REMOVE_NETWORK_WIDE_OPTION = 0x40;

  public static SerialRequest createSerialRequest(RemoveNodeFromNeworkMode mode, byte callbackFlowId, boolean networkWide) {
    FrameBuffer buffer = startUpFrameBuffer(FRAME_CONTROL_SIZE + 2, REMOVE_NODE_FROM_NETWORK)
        .put((byte) (mode.getCode() | (networkWide ? REMOVE_NETWORK_WIDE_OPTION : 0x00)))
        .put(callbackFlowId)
        .putCRC();
    return SerialRequest.builder()
        .responseExpected(false)
        .frameData(buffer)
        .serialCommand(REMOVE_NODE_FROM_NETWORK)
        .callbackFlowId(callbackFlowId)
        .build();
  }

  public static SerialRequest createStartRemoveAnyNodeRequest(byte callbackFlowId) {
    return createSerialRequest(RemoveNodeFromNeworkMode.REMOVE_NODE_ANY, callbackFlowId, true);
  }

  public static SerialRequest createFinalTransactionRequest() {
    return createSerialRequest(RemoveNodeFromNeworkMode.REMOVE_NODE_ANY, (byte) 0, true);
  }
}
