package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SEND_DATA;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.buffers.DisposableFrameBuffer;
import com.rposcro.jwavez.serial.model.TransmitOption;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class SendDataRequest extends ZWaveRequest {

  public static SerialRequest createSendDataRequest(NodeId addresseeId, ZWaveControlledCommand zWaveCommand, byte callbackFlowId) {
    DisposableFrameBuffer buffer = startUpFrameBuffer(FRAME_CONTROL_SIZE + 4 + zWaveCommand.getPayloadLength(), SEND_DATA)
        .put(addresseeId.getId())
        .put((byte) zWaveCommand.getPayloadLength());
    ImmutableBuffer cmdBuffer = zWaveCommand.getPayload().rewind();
    while (cmdBuffer.hasNext()) {
      buffer.put(cmdBuffer.next());
    }
    buffer.put((byte) (TransmitOption.TRANSMIT_OPTION_ACK.getCode() | TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE.getCode()))
        .put(callbackFlowId)
        .putCRC();

    return SerialRequest.builder()
        .responseExpected(true)
        .serialCommand(SEND_DATA)
        .frameData(buffer)
        .callbackFlowId(callbackFlowId)
        .build();
  }
}
