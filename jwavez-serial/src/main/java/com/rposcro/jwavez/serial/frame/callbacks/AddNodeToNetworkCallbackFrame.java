package com.rposcro.jwavez.serial.frame.callbacks;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.frame.CallbackFrameModel;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.constants.AddNodeToNeworkStatus;
import com.rposcro.jwavez.serial.utils.NodeUtil;
import java.util.Optional;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.ADD_NODE_TO_NETWORK)
public class AddNodeToNetworkCallbackFrame extends SOFCallbackFrame {

  private static final int OFFSET_STATUS = OFFSET_PAYLOAD + 1;
  private static final int OFFSET_SOURCE = OFFSET_PAYLOAD + 2;
  private static final int OFFSET_NIF_LENGTH = OFFSET_PAYLOAD + 3;

  private AddNodeToNeworkStatus status;
  private Optional<NodeInfo> nodeInfo;

  public AddNodeToNetworkCallbackFrame(byte[] buffer) {
    super(buffer);
    this.status = AddNodeToNeworkStatus.ofCode(buffer[OFFSET_STATUS]);
    if (isNodeInfoPresent(buffer)) {
      this.nodeInfo = Optional.of(NodeUtil.decodeNodeInfo(buffer, OFFSET_PAYLOAD + 2));
    } else {
      this.nodeInfo = Optional.empty();
    }
  }

  private boolean isNodeInfoPresent(byte[] buffer) {
    return (buffer[OFFSET_SOURCE] != 0 && buffer[OFFSET_NIF_LENGTH] != 0);
  }
}
