package com.rposcro.jwavez.serial.frame.requests;

import static com.rposcro.jwavez.serial.utils.FieldUtil.booleanByte;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.SERIAL_API_SETUP)
public class SerilaAPISetupRequestFrame extends SOFRequestFrame {

  public SerilaAPISetupRequestFrame(boolean txStatusReportEnabled) {
    super(SerialCommand.SERIAL_API_SETUP,
        booleanByte(txStatusReportEnabled));
  }
}
