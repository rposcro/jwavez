package com.rposcro.jwavez.serial.probe.frame.callbacks;

import com.rposcro.jwavez.serial.probe.frame.CallbackFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@CallbackFrameModel(function = SerialCommand.SET_DEFAULT)
public class SetDefaultCallbackFrame extends SOFCallbackFrame {

  public SetDefaultCallbackFrame(byte[] buffer) {
    super(buffer);
  }
}
