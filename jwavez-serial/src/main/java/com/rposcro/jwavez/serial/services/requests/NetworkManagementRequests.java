package com.rposcro.jwavez.serial.services.requests;

import static com.rposcro.jwavez.serial.utils.FieldUtil.booleanByte;
import static com.rposcro.jwavez.serial.utils.FrameUtil.frameCRC;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.buffers.dispatchers.BufferDispatcher;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.model.TransmitOption;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class NetworkManagementRequests extends AbstractFrameRequests {

  public NetworkManagementRequests(BufferDispatcher bufferDispatcher) {
    super(bufferDispatcher);
  }

  public SerialRequest setLocalSUCNodeIdRequest(NodeId localControllerId, boolean enableSucAndSis) {
    FrameBuffer buffer = frameBuffer(SerialCommand.SET_SUC_NODE_ID, FRAME_CONTROL_SIZE + 5);
    buffer.put(localControllerId.getId())
        .put(booleanByte(enableSucAndSis))
        .put(booleanByte(false))
        .put(booleanByte(enableSucAndSis))
        .put((byte) 0x00)
        .put(frameCRC(buffer.asByteBuffer()));
    return commandRequest(buffer, true);
  }

  public SerialRequest setSUCNodeIdRequest(NodeId localControllerId, boolean enableSucAndSis, byte callbackFunctionId) {
    FrameBuffer buffer = frameBuffer(SerialCommand.SET_SUC_NODE_ID, FRAME_CONTROL_SIZE + 5);
    buffer.put(localControllerId.getId())
        .put(booleanByte(enableSucAndSis))
        .put(booleanByte(false))
        .put(booleanByte(enableSucAndSis))
        .put(callbackFunctionId)
        .put(frameCRC(buffer.asByteBuffer()));
    return commandRequest(buffer, true);
  }

  public SerialRequest sendSUCIdRequest(NodeId addresseeId, byte callbackFunctionId) {
    FrameBuffer buffer = frameBuffer(SerialCommand.SET_SUC_NODE_ID, FRAME_CONTROL_SIZE + 3);
    buffer.put(addresseeId.getId())
        .put(defaultTransmitOptions())
        .put(callbackFunctionId)
        .put(frameCRC(buffer.asByteBuffer()));
    return commandRequest(buffer, true);
  }

  private byte defaultTransmitOptions() {
    return (byte) (TransmitOption.TRANSMIT_OPTION_ACK.getCode() | TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE.getCode());
  }
}
