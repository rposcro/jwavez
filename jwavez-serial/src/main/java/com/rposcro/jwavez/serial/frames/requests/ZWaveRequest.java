package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.serial.buffers.DisposableFrameBuffer;
import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.FrameRequest;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;

public class ZWaveRequest {

  protected static FrameBuffer startFrameBuffer(int capacity, SerialCommand command) {
    return new DisposableFrameBuffer(capacity)
        .put(SerialFrameConstants.CATEGORY_SOF)
        .put((byte) (capacity - 2))
        .put(SerialFrameConstants.TYPE_REQ)
        .put(command.getCode());
  }

  protected static FrameBuffer completeFrameBuffer(SerialCommand command) {
    return new DisposableFrameBuffer(5)
        .put(SerialFrameConstants.CATEGORY_SOF)
        .put((byte) (3))
        .put(SerialFrameConstants.TYPE_REQ)
        .put(command.getCode())
        .putCRC();
  }

  protected static FrameRequest nonPayloadRequest(SerialCommand command) {
    return FrameRequest.builder()
        .responseExpected(true)
        .frameData(completeFrameBuffer(command))
        .serialCommand(command)
        .build();
  }
}
