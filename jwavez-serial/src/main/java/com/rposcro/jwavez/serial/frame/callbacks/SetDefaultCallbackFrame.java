package com.rposcro.jwavez.serial.frame.callbacks;

import com.rposcro.jwavez.serial.frame.CallbackFrameModel;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
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
