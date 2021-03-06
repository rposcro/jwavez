package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_PROTOCOL_STATUS)
public class GetProtocolStatusResponse extends ZWaveResponse {

  private byte returnValue;

  public GetProtocolStatusResponse(ViewBuffer frameBuffer) {
    super(frameBuffer);
    this.returnValue = frameBuffer.get(FRAME_OFFSET_PAYLOAD);
  }
}
