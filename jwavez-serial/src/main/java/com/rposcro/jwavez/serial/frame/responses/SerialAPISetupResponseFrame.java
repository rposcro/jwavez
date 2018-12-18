package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.utils.FieldUtil;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.SERIAL_API_SETUP)
public class SerialAPISetupResponseFrame extends SOFResponseFrame {

  @Getter
  private boolean successful;

  public SerialAPISetupResponseFrame(byte[] buffer) {
    super(buffer);
    this.successful = FieldUtil.byteBoolean(buffer[OFFSET_PAYLOAD]);
  }
}
