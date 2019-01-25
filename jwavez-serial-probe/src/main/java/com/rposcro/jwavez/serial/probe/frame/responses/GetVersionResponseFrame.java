package com.rposcro.jwavez.serial.probe.frame.responses;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFResponseFrame;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_VERSION)
public class GetVersionResponseFrame extends SOFResponseFrame {

  private String version;
  private byte responseData;

  public GetVersionResponseFrame(byte[] buffer) {
    super(buffer);
    this.version = new String(buffer, 4, 12);
    this.responseData = buffer[16];
  }
}
