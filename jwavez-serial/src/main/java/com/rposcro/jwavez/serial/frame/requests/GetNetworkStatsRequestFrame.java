package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.GET_NETWORK_STATS)
public class GetNetworkStatsRequestFrame extends SOFRequestFrame {

  public GetNetworkStatsRequestFrame() {
    super(GetNetworkStatsRequestFrame.class.getAnnotation(RequestFrameModel.class).function());
  }
}
