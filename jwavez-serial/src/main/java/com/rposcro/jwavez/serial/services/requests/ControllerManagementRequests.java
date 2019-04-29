package com.rposcro.jwavez.serial.services.requests;

import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.buffers.dispatchers.BufferDispatcher;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.FieldUtil;
import com.rposcro.jwavez.serial.utils.FrameUtil;
import lombok.Builder;

public class ControllerManagementRequests extends AbstractFrameRequests {

  @Builder
  public ControllerManagementRequests(BufferDispatcher bufferDispatcher) {
    super(bufferDispatcher);
  }

  public SerialRequest enableSUCRequest() {
    return commandRequest(SerialCommand.ENABLE_SUC, true);
  }

  public SerialRequest serialAPISetupRequest(boolean txStatusReportEnabled) {
    FrameBuffer buffer = frameBuffer(SerialCommand.ADD_NODE_TO_NETWORK, FRAME_CONTROL_SIZE + 1);
    buffer.put(FieldUtil.booleanByte(txStatusReportEnabled))
        .put(FrameUtil.frameCRC(buffer.asByteBuffer()));
    return commandRequest(buffer, true);
  }

  public SerialRequest setDefaultRequest() {
    return commandRequest(SerialCommand.SET_DEFAULT, false);
  }

}
