package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.utils.FieldUtil;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.SEND_DATA)
public class SendDataResponseFrame extends SOFResponseFrame {

  private boolean sendingQueued;

  public SendDataResponseFrame(byte[] buffer) {
    super(buffer);
    sendingQueued = FieldUtil.byteBoolean(buffer[OFFSET_PAYLOAD]);
  }
}
