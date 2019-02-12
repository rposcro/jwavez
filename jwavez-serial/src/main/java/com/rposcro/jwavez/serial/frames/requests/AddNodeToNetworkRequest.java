package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.ADD_NODE_TO_NETWORK;

import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkMode;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class AddNodeToNetworkRequest extends ZWaveRequest {

  public static SerialRequest createSerialRequest(AddNodeToNeworkMode mode, byte callbackFunctionId, boolean networkWide, boolean normalPower) {
    FrameBuffer buffer = startUpFrameBuffer(FRAME_CONTROL_SIZE + 2, ADD_NODE_TO_NETWORK)
        .put((byte) (mode.getCode() | (networkWide ? 0x40 : 0x00) | (normalPower ? 0x80 : 0x00)))
        .put(callbackFunctionId)
        .putCRC();
    return SerialRequest.builder()
        .responseExpected(false)
        .frameData(buffer)
        .serialCommand(ADD_NODE_TO_NETWORK)
        .build();
  }

  public static SerialRequest createStartAddAnyNodeRequest(byte callbackFunctionId) {
    return createSerialRequest(AddNodeToNeworkMode.ADD_NODE_ANY, callbackFunctionId, true, true);
  }

  public static SerialRequest createStopTransactionRequest(byte callbackFunctionId) {
    return createSerialRequest(AddNodeToNeworkMode.ADD_NODE_STOP, callbackFunctionId, true, true);
  }

  public static SerialRequest createStopFailedTransactionRequest(byte callbackFunctionId) {
    return createSerialRequest(AddNodeToNeworkMode.ADD_NODE_STOP_FAILED, callbackFunctionId, true, true);
  }

  public static SerialRequest createFinalTransactionRequest() {
    return createSerialRequest(AddNodeToNeworkMode.ADD_NODE_ANY, (byte) 0, true, true);
  }
}
