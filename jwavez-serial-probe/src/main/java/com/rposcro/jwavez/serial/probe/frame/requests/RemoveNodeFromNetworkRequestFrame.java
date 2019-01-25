package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.RemoveNodeFromNeworkMode;

@RequestFrameModel(function = SerialCommand.REMOVE_NODE_FROM_NETWORK)
public class RemoveNodeFromNetworkRequestFrame extends SOFRequestFrame {

  private static final int REMOVE_NETWORK_WIDE_OPTION = 0x40;

  public RemoveNodeFromNetworkRequestFrame(RemoveNodeFromNeworkMode mode, byte callbackFunctionId, boolean networkWide) {
    super(RemoveNodeFromNetworkRequestFrame.class.getAnnotation(RequestFrameModel.class).function(),
          (byte) (mode.getCode() | (networkWide ? REMOVE_NETWORK_WIDE_OPTION : 0x00)),
          callbackFunctionId);
  }

  public RemoveNodeFromNetworkRequestFrame(RemoveNodeFromNeworkMode mode, byte callbackFunctionId) {
    this(mode, callbackFunctionId, false);
  }

  public RemoveNodeFromNetworkRequestFrame(RemoveNodeFromNeworkMode mode) {
    this(mode, (byte) 0x00, false);
  }
}
