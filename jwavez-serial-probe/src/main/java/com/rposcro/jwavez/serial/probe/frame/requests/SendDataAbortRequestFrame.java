package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.SEND_DATA_ABORT)
public class SendDataAbortRequestFrame extends SOFRequestFrame {

  public SendDataAbortRequestFrame() {
    super(SerialCommand.SEND_DATA_ABORT);
  }
}
