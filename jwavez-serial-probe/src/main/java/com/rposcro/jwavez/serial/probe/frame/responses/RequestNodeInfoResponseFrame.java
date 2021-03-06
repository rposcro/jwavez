package com.rposcro.jwavez.serial.probe.frame.responses;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.probe.utils.FieldUtil;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.REQUEST_NODE_INFO)
public class RequestNodeInfoResponseFrame extends SOFResponseFrame {

  @Getter
  private boolean successful;

  public RequestNodeInfoResponseFrame(byte[] buffer) {
    super(buffer);
    this.successful = FieldUtil.byteBoolean(buffer[OFFSET_PAYLOAD]);
  }
}
