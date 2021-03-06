package com.rposcro.jwavez.serial.services.requests;

import static com.rposcro.jwavez.serial.utils.FrameUtil.frameCRC;

import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.buffers.dispatchers.BufferDispatcher;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkMode;
import com.rposcro.jwavez.serial.model.LearnMode;
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkMode;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import lombok.Builder;

public class InclusionExclusionRequests extends AbstractFrameRequests {

  private static final int REMOVE_NETWORK_WIDE_OPTION = 0x40;

  @Builder
  public InclusionExclusionRequests(BufferDispatcher bufferDispatcher) {
    super(bufferDispatcher);
  }

  public SerialRequest addNodeToNetworkRequest(AddNodeToNeworkMode mode, boolean networkWide, boolean normalPower, byte callbackFlowId) {
    FrameBuffer buffer = frameBuffer(SerialCommand.ADD_NODE_TO_NETWORK, FRAME_CONTROL_SIZE + 2);
    buffer.put((byte) (mode.getCode() | (networkWide ? 0x40 : 0x00) | (normalPower ? 0x80 : 0x00)))
        .put(callbackFlowId)
        .put(frameCRC(buffer.asByteBuffer()));
    return SerialRequest.builder()
        .responseExpected(false)
        .frameData(buffer)
        .build();
  }

  public SerialRequest removeNodeFromNetworkRequest(RemoveNodeFromNeworkMode mode, boolean networkWide, byte callbackFlowId) {
    FrameBuffer buffer = frameBuffer(SerialCommand.ADD_NODE_TO_NETWORK, FRAME_CONTROL_SIZE + 2);
    buffer.put((byte) (mode.getCode() | (networkWide ? REMOVE_NETWORK_WIDE_OPTION : 0x00)))
        .put(callbackFlowId)
        .put(frameCRC(buffer.asByteBuffer()));
    return commandRequest(buffer, false);
  }

  public SerialRequest setLearnModeRequest(LearnMode learnMode, byte callbackFlowId) {
    FrameBuffer buffer = frameBuffer(SerialCommand.ADD_NODE_TO_NETWORK, FRAME_CONTROL_SIZE + 2);
    buffer.put((byte) learnMode.getCode())
        .put(callbackFlowId)
        .put(frameCRC(buffer.asByteBuffer()));
    return commandRequest(buffer, true);
  }
}
