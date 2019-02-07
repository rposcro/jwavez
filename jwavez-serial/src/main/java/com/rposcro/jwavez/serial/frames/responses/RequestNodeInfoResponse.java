package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import com.rposcro.jwavez.serial.utils.FieldUtil;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.REQUEST_NODE_INFO)
public class RequestNodeInfoResponse extends Response {

  @Getter
  private boolean requestAccepted;

  public RequestNodeInfoResponse(ViewBuffer frameBuffer) {
    super(frameBuffer);
    this.requestAccepted = FieldUtil.byteBoolean(frameBuffer.get(FRAME_OFFSET_PAYLOAD));
  }
}
