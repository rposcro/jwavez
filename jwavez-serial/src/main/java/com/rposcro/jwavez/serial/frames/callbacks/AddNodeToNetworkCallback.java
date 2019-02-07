package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus;
import com.rposcro.jwavez.serial.utils.NodeUtil;
import java.util.Optional;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.ADD_NODE_TO_NETWORK)
public class AddNodeToNetworkCallback extends FunctionCallback {

  private static final int OFFSET_SOURCE_NODE_ID = FRAME_OFFSET_PAYLOAD + 2;
  private static final int OFFSET_NIF_LENGTH = FRAME_OFFSET_PAYLOAD + 3;

  private AddNodeToNeworkStatus status;
  private Optional<NodeInfo> nodeInfo;

  public AddNodeToNetworkCallback(ViewBuffer frameBuffer) {
    super(frameBuffer);
    this.status = AddNodeToNeworkStatus.ofCode(frameBuffer.get());

    if (isNodeInfoPresent(frameBuffer)) {
      this.nodeInfo = Optional.of(NodeUtil.decodeNodeInfo(frameBuffer));
    } else {
      this.nodeInfo = Optional.empty();
    }
  }

  private boolean isNodeInfoPresent(ViewBuffer frameBuffer) {
    return (frameBuffer.get(OFFSET_SOURCE_NODE_ID) != 0 && frameBuffer.get(OFFSET_NIF_LENGTH) != 0);
  }
}
