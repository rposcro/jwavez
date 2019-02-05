package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.utils.FrameUtil.frameCRC;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.buffers.dispatchers.BufferDispatcher;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.FrameRequest;

public class NetworkInformationRequests extends AbstractFrameRequests {

  public NetworkInformationRequests(BufferDispatcher bufferDispatcher) {
    super(bufferDispatcher);
  }

  public FrameRequest getSUCNodeIdRequest() {
    return commandRequest(SerialCommand.GET_SUC_NODE_ID, true);
  }

  public FrameRequest getNetworkStatsRequest() {
    return commandRequest(SerialCommand.GET_NETWORK_STATS, true);
  }

  public FrameRequest requestNodeInfoRequest(NodeId nodeId) {
    FrameBuffer buffer = frameBuffer(SerialCommand.ADD_NODE_TO_NETWORK, FRAME_CONTROL_SIZE + 1);
    buffer.put(nodeId.getId())
        .put(frameCRC(buffer.asByteBuffer()));
    return FrameRequest.builder()
        .responseExpected(false)
        .frameData(buffer)
        .build();
  }
}
