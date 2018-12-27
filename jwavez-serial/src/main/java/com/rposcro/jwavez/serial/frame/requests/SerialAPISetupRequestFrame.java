package com.rposcro.jwavez.serial.frame.requests;

import static com.rposcro.jwavez.serial.utils.FieldUtil.booleanByte;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;

@RequestFrameModel(function = SerialCommand.SERIAL_API_SETUP)
public class SerialAPISetupRequestFrame extends SOFRequestFrame {

  public SerialAPISetupRequestFrame(boolean txStatusReportEnabled) {
    super(SerialCommand.SERIAL_API_SETUP,
        booleanByte(txStatusReportEnabled));
  }

}