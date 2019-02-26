package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SEND_SUC_ID;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.model.TransmitOption;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class SendSUCIdRequest extends ZWaveRequest {

  public static SerialRequest createSerialRequest(NodeId addresseeId, byte callbackFunctionId) {
    return SerialRequest.builder()
        .serialCommand(SEND_SUC_ID)
        .responseExpected(true)
        .frameData(startUpFrameBuffer(FRAME_CONTROL_SIZE + 3, SEND_SUC_ID)
            .put(addresseeId.getId())
            .put((byte) (TransmitOption.TRANSMIT_OPTION_ACK.getCode() | TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE.getCode()))
            .put(callbackFunctionId)
            .putCRC())
        .callbackFunctionId(callbackFunctionId)
        .build();
  }
}
