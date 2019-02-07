package com.rposcro.jwavez.serial.services.requests;

import static com.rposcro.jwavez.serial.utils.FrameUtil.frameCRC;

import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.buffers.dispatchers.BufferDispatcher;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkMode;
import com.rposcro.jwavez.serial.model.LearnMode;
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkMode;
import com.rposcro.jwavez.serial.rxtx.FrameRequest;
import lombok.Builder;

public class InclusionExclusionRequests extends AbstractFrameRequests {

  private static final int REMOVE_NETWORK_WIDE_OPTION = 0x40;

  @Builder
  public InclusionExclusionRequests(BufferDispatcher bufferDispatcher) {
    super(bufferDispatcher);
  }

  public FrameRequest addNodeToNetworkRequest(AddNodeToNeworkMode mode, boolean networkWide, boolean normalPower, byte callbackFunctionId) {
    FrameBuffer buffer = frameBuffer(SerialCommand.ADD_NODE_TO_NETWORK, FRAME_CONTROL_SIZE + 2);
    buffer.put((byte) (mode.getCode() | (networkWide ? 0x40 : 0x00) | (normalPower ? 0x80 : 0x00)))
        .put(callbackFunctionId)
        .put(frameCRC(buffer.asByteBuffer()));
    return FrameRequest.builder()
        .responseExpected(false)
        .frameData(buffer)
        .build();
  }

  public FrameRequest removeNodeFromNetworkRequest(RemoveNodeFromNeworkMode mode, boolean networkWide, byte callbackFunctionId) {
    FrameBuffer buffer = frameBuffer(SerialCommand.ADD_NODE_TO_NETWORK, FRAME_CONTROL_SIZE + 2);
    buffer.put((byte) (mode.getCode() | (networkWide ? REMOVE_NETWORK_WIDE_OPTION : 0x00)))
        .put(callbackFunctionId)
        .put(frameCRC(buffer.asByteBuffer()));
    return commandRequest(buffer, false);
  }

  public FrameRequest setLearnModeRequest(LearnMode learnMode, byte callbackFunctionId) {
    FrameBuffer buffer = frameBuffer(SerialCommand.ADD_NODE_TO_NETWORK, FRAME_CONTROL_SIZE + 2);
    buffer.put((byte) learnMode.getCode())
        .put(callbackFunctionId)
        .put(frameCRC(buffer.asByteBuffer()));
    return commandRequest(buffer, true);
  }
}
