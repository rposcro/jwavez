package com.rposcro.jwavez.serial.services.requests;

import static com.rposcro.jwavez.serial.utils.FrameUtil.frameCRC;

import com.rposcro.jwavez.serial.buffers.dispatchers.BufferDispatcher;
import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import com.rposcro.jwavez.serial.rxtx.FrameRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractFrameRequests {

  protected static final int FRAME_CONTROL_SIZE = 5;

  private BufferDispatcher bufferDispatcher;

  protected FrameRequest commandRequest(SerialCommand command, boolean responseExpected) {
    FrameBuffer buffer = frameBuffer(command, FRAME_CONTROL_SIZE);
    buffer.put(frameCRC(buffer.asByteBuffer()));
    return FrameRequest.builder()
        .frameData(buffer)
        .responseExpected(responseExpected)
        .build();
  }

  protected FrameRequest commandRequest(FrameBuffer frameBuffer, boolean responseExpected) {
    return FrameRequest.builder()
        .frameData(frameBuffer)
        .responseExpected(responseExpected)
        .build();
  }

  protected FrameBuffer frameBuffer(SerialCommand command, int bufferSize) {
    FrameBuffer buffer = bufferDispatcher.allocateBuffer(bufferSize);
    buffer.put(SerialFrameConstants.TYPE_REQ)
        .put((byte) (FRAME_CONTROL_SIZE - 2))
        .put(SerialFrameConstants.CATEGORY_SOF)
        .put(command.getCode());
    return buffer;
  }
}
