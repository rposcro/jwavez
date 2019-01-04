package com.rposcro.jwavez.serial.frame.callbacks;

import com.rposcro.jwavez.serial.frame.CallbackFrameModel;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.constants.TransmitCompletionStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@CallbackFrameModel(function = SerialCommand.SEND_SUC_ID)
public class SendSUCIdCallbackFrame extends SOFCallbackFrame {

  private static final int OFFSET_TRANSMIT_STATUS = OFFSET_FUNC_ID + 1;

  private TransmitCompletionStatus txStatus;
  private boolean statusReportPresent;

  public SendSUCIdCallbackFrame(byte[] buffer) {
    super(buffer);
    this.txStatus = TransmitCompletionStatus.ofCode(buffer[OFFSET_TRANSMIT_STATUS]);
    this.statusReportPresent = getPayloadSize() > 2;
  }
}
