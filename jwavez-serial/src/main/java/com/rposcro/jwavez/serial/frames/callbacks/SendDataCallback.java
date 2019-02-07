package com.rposcro.jwavez.serial.frames.callbacks;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.SEND_DATA)
public class SendDataCallback extends FunctionCallback {

  private TransmitCompletionStatus transmitCompletionStatus;
  private boolean statusReportPresent;

  public SendDataCallback(ViewBuffer frameBuffer) {
    super(frameBuffer);
    this.transmitCompletionStatus = TransmitCompletionStatus.ofCode(frameBuffer.get());
    this.statusReportPresent = frameBuffer.remaining() > 1;
  }
}
