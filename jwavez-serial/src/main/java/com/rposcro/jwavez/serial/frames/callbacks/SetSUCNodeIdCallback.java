package com.rposcro.jwavez.serial.frames.callbacks;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.utils.FieldUtil;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.SET_SUC_NODE_ID)
public class SetSUCNodeIdCallback extends FlowCallback {

  private boolean successful;

  public SetSUCNodeIdCallback(ViewBuffer frameBuffer) {
    super(frameBuffer);
    this.successful = FieldUtil.byteBoolean(frameBuffer.get());
  }
}
