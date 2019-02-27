package com.rposcro.jwavez.serial.services.requests;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.buffers.dispatchers.BufferDispatcher;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.model.TransmitOption;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.FrameUtil;
import lombok.Builder;

public class SendDataRequests extends AbstractFrameRequests {

  @Builder
  public SendDataRequests(BufferDispatcher bufferDispatcher) {
    super(bufferDispatcher);
  }

  public SerialRequest SendDataRequestFrame(NodeId addresseeId, byte callbackFlowId, ZWaveControlledCommand zWaveCommand) {
    FrameBuffer buffer = frameBuffer(SerialCommand.ADD_NODE_TO_NETWORK, FRAME_CONTROL_SIZE + 4 + zWaveCommand.getPayloadLength())
        .put(addresseeId.getId())
        .put((byte) zWaveCommand.getPayloadLength());

    ImmutableBuffer payloadBuffer = zWaveCommand.getPayloadBuffer();
    while (payloadBuffer.hasNext()) {
      buffer.put(payloadBuffer.next());
    }

    buffer.put(defaultTransmitOptions())
        .put(callbackFlowId)
        .put(FrameUtil.frameCRC(buffer.asByteBuffer()));

    return SerialRequest.builder()
        .responseExpected(false)
        .frameData(buffer)
        .build();
  }

  private static byte defaultTransmitOptions() {
    return (byte) (TransmitOption.TRANSMIT_OPTION_ACK.getCode() | TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE.getCode());
  }
}
