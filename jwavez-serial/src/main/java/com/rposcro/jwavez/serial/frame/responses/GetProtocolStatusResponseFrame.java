package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_PROTOCOL_STATUS)
public class GetProtocolStatusResponseFrame extends SOFResponseFrame {

  private byte returnValue;

  public GetProtocolStatusResponseFrame(byte[] buffer) {
    super(buffer);
    this.returnValue = buffer[OFFSET_PAYLOAD];
  }
}
