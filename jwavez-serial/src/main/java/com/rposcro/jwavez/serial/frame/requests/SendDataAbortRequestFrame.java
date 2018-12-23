package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.SEND_DATA_ABORT)
public class SendDataAbortRequestFrame extends SOFRequestFrame {

  public SendDataAbortRequestFrame() {
    super(SerialCommand.SEND_DATA_ABORT);
  }
}
