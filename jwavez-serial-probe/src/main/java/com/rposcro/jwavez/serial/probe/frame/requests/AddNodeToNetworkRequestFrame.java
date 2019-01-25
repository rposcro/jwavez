package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.AddNodeToNeworkMode;

@RequestFrameModel(function = SerialCommand.ADD_NODE_TO_NETWORK)
public class AddNodeToNetworkRequestFrame extends SOFRequestFrame {

  public AddNodeToNetworkRequestFrame(AddNodeToNeworkMode mode, byte callbackFunctionId, boolean networkWide, boolean normalPower) {
    super(
        SerialCommand.ADD_NODE_TO_NETWORK,
        (byte) (mode.getCode() | (networkWide ? 0x40 : 0x00) | (normalPower ? 0x80 : 0x00)),
        callbackFunctionId);
  }

  public AddNodeToNetworkRequestFrame(AddNodeToNeworkMode mode, byte callbackFunctionId) {
    super(SerialCommand.ADD_NODE_TO_NETWORK, mode.getCode(), callbackFunctionId);
  }

  public AddNodeToNetworkRequestFrame(AddNodeToNeworkMode mode) {
    super(SerialCommand.ADD_NODE_TO_NETWORK, mode.getCode(), (byte) 0);
  }
}
