package com.rposcro.jwavez.serial.probe.frame.callbacks;

import com.rposcro.jwavez.serial.probe.frame.CallbackFrameModel;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.TransmitCompletionStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@CallbackFrameModel(function = SerialCommand.SEND_DATA)
public class SendDataCallbackFrame extends SOFCallbackFrame {

  private static final int OFFSET_TRANSMIT_STATUS = OFFSET_FUNC_ID + 1;

  private TransmitCompletionStatus txStatus;
  private boolean statusReportPresent;

  public SendDataCallbackFrame(byte[] buffer) {
    super(buffer);
    this.txStatus = TransmitCompletionStatus.ofCode(buffer[OFFSET_TRANSMIT_STATUS]);
    this.statusReportPresent = getPayloadSize() > 2;
  }
}
