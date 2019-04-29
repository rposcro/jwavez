package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.GET_PROTOCOL_STATUS)
public class GetProtocolStatusRequestFrame extends SOFRequestFrame {

  public GetProtocolStatusRequestFrame() {
    super(GetProtocolStatusRequestFrame.class.getAnnotation(RequestFrameModel.class).function());
  }
}
