package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.GET_NETWORK_STATS)
public class GetNetworkStatsRequestFrame extends SOFRequestFrame {

  public GetNetworkStatsRequestFrame() {
    super(GetNetworkStatsRequestFrame.class.getAnnotation(RequestFrameModel.class).function());
  }
}
