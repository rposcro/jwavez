package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.utils.FieldUtil;

@RequestFrameModel(function = SerialCommand.SERIAL_API_SETUP)
public class SerialAPISetupRequestFrame extends SOFRequestFrame {

  public SerialAPISetupRequestFrame(boolean txStatusReportEnabled) {
    super(SerialCommand.SERIAL_API_SETUP,
        FieldUtil.booleanByte(txStatusReportEnabled));
  }

}