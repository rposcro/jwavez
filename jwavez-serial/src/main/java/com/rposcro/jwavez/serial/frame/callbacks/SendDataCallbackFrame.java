package com.rposcro.jwavez.serial.frame.callbacks;

import com.rposcro.jwavez.serial.frame.CallbackFrameModel;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.constants.TransmitCompletionStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@CallbackFrameModel(function = SerialCommand.SEND_DATA)
public class SendDataCallbackFrame extends SOFCallbackFrame {

  private static final int OFFSET_TRANSMIT_STATUS = OFFSET_PAYLOAD + 1;

  private TransmitCompletionStatus status;

  public SendDataCallbackFrame(byte[] buffer) {
    super(buffer);
    this.status = TransmitCompletionStatus.ofCode(buffer[OFFSET_TRANSMIT_STATUS]);
  }
}
