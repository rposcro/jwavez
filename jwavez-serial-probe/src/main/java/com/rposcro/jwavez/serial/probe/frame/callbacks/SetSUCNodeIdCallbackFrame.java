package com.rposcro.jwavez.serial.probe.frame.callbacks;

import com.rposcro.jwavez.serial.probe.frame.CallbackFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.utils.FieldUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@CallbackFrameModel(function = SerialCommand.SET_SUC_NODE_ID)
public class SetSUCNodeIdCallbackFrame extends SOFCallbackFrame {

  private static final int OFFSET_STATUS = OFFSET_PAYLOAD + 1;

  private boolean successful;

  public SetSUCNodeIdCallbackFrame(byte[] buffer) {
    super(buffer);
    this.successful = FieldUtil.byteBoolean(buffer[OFFSET_STATUS]);
  }
}
