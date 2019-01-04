package com.rposcro.jwavez.serial.frame.responses;

import static com.rposcro.jwavez.serial.utils.FieldUtil.byteBoolean;

import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.SEND_SUC_ID)
public class SendSUCIdResponseFrame extends SOFResponseFrame {

  private boolean requestAccepted;

  public SendSUCIdResponseFrame(byte[] buffer) {
    super(buffer);
    this.requestAccepted = byteBoolean(buffer[OFFSET_PAYLOAD]);
  }
}
