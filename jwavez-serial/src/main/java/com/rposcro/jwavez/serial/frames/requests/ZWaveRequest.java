package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.serial.buffers.DisposableFrameBuffer;
import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;

public class ZWaveRequest {

  protected final static int FRAME_CONTROL_SIZE = 5;

  protected static DisposableFrameBuffer startUpFrameBuffer(int capacity, SerialCommand command) {
    return new DisposableFrameBuffer(capacity)
        .put(SerialFrameConstants.CATEGORY_SOF)
        .put((byte) (capacity - 2))
        .put(SerialFrameConstants.TYPE_REQ)
        .put(command.getCode());
  }

  private static FrameBuffer completeFrameBuffer(SerialCommand command) {
    return new DisposableFrameBuffer(5)
        .put(SerialFrameConstants.CATEGORY_SOF)
        .put((byte) (3))
        .put(SerialFrameConstants.TYPE_REQ)
        .put(command.getCode())
        .putCRC();
  }

  protected static SerialRequest nonPayloadRequest(SerialCommand command) {
    return SerialRequest.builder()
        .responseExpected(true)
        .frameData(completeFrameBuffer(command))
        .serialCommand(command)
        .build();
  }

  public static SerialRequest ofFrameData(SerialCommand command, byte... data) {
    return SerialRequest.builder()
        .responseExpected(false)
        .serialCommand(command)
        .frameData(new DisposableFrameBuffer(data.length + 5)
            .put(SerialFrameConstants.CATEGORY_SOF)
            .put((byte) (data.length + 3))
            .put(SerialFrameConstants.TYPE_REQ)
            .put(command.getCode())
            .putData(data)
            .putCRC()
      ).build();
  }
}
