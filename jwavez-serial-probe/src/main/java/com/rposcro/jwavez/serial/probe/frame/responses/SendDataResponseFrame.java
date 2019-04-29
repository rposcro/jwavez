package com.rposcro.jwavez.serial.probe.frame.responses;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.probe.utils.FieldUtil;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.SEND_DATA)
public class SendDataResponseFrame extends SOFResponseFrame {

  private boolean requestAccepted;

  public SendDataResponseFrame(byte[] buffer) {
    super(buffer);
    requestAccepted = FieldUtil.byteBoolean(buffer[OFFSET_PAYLOAD]);
  }
}
