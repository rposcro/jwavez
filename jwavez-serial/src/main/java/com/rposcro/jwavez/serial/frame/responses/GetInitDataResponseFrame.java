package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_INIT_DATA)
public class GetInitDataResponseFrame extends SOFResponseFrame {

  private static final int OFFSET_VERSION = 4;
  private static final int OFFSET_CAPABILITIES = 5;
  private static final int OFFSET_NODES_MASK = 7;
  private static final int OFFSET_CHIP_TYPE = 36;
  private static final int OFFSET_CHIP_VERSION = 37;

  private static final int NODE_BITMASK_SIZE = 29;

  private byte version;
  private byte capabilities;
  private byte chipType;
  private byte chipVersion;
  private List<NodeId> nodes = new ArrayList<>();

  public GetInitDataResponseFrame(byte[] buffer) {
    super(buffer);
    this.version = buffer[OFFSET_VERSION];
    this.capabilities = buffer[OFFSET_CAPABILITIES];
    this.chipType = buffer[OFFSET_CHIP_TYPE];
    this.chipVersion = buffer[OFFSET_CHIP_VERSION];
    parseNodes(buffer);
  }

  public List<NodeId> getNodeList() {
    return Collections.unmodifiableList(nodes);
  }

  private void parseNodes(byte[] buffer) {
    for (int byteIdx = 0; byteIdx < NODE_BITMASK_SIZE; byteIdx++) {
      byte maskByte = buffer[OFFSET_NODES_MASK + byteIdx];
      for (int bitIdx = 0; bitIdx < 8; bitIdx++) {
        byte nodeId = (byte)((byteIdx * 8) + bitIdx + 1);
        if ((maskByte & (0x01 << bitIdx)) > 0) {
          nodes.add(new NodeId(nodeId));
        }
      }
    }
  }
}
