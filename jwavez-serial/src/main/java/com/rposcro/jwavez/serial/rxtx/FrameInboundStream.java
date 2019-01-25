package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_NAK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_SOF;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.MAX_Z_WAVE_FRAME_SIZE;

import com.rposcro.jwavez.serial.exceptions.SerialStreamException;
import com.rposcro.jwavez.serial.utils.ViewBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import lombok.Builder;

@Builder
public class FrameInboundStream {

  private static final long FRAME_COMPLETE_TIMEOUT = 1500;

  private SerialConnection serialConnection;

  private final ByteBuffer frameBuffer;
  private final ViewBuffer viewBuffer;

  public FrameInboundStream() {
    this.frameBuffer = ByteBuffer.allocateDirect(MAX_Z_WAVE_FRAME_SIZE * 2);
    this.frameBuffer.limit(0);
    this.viewBuffer = new ViewBuffer(frameBuffer);
  }

  public ViewBuffer nextFrame() throws SerialStreamException, IOException {
    if (!frameBuffer.hasRemaining()) {
      purgeAndLoadBuffer();
    } else {
      setViewOverFrame();
    }

    return viewBuffer;
  }

  public void purgeStream() throws IOException {
    do {
      frameBuffer.position(0).limit(frameBuffer.capacity());
    } while (serialConnection.readData(frameBuffer) > 0);
    frameBuffer.position(0).limit(0);
  }

  private ViewBuffer setViewOverFrame() throws SerialStreamException, IOException {
    int position = frameBuffer.position();
    byte category = frameBuffer.get(position);

    if (category == CATEGORY_ACK || category == CATEGORY_CAN || category == CATEGORY_NAK ) {
      viewBuffer.setViewRange(position, 1);
    } else if (category == CATEGORY_SOF) {
      return setViewOverSOF();
    } else {
      throw new SerialStreamException("Unrecognized frame category %0X2", category);
    }
    return viewBuffer;
  }

  private ViewBuffer setViewOverSOF() throws SerialStreamException, IOException {
    int position = frameBuffer.position();
    ensureRemaining(3);
    int length = frameBuffer.get(position + 2) + 2;
    ensureRemaining(length);
    viewBuffer.setViewRange(position, length);
    return viewBuffer;
  }

  private void ensureRemaining(int expectedRemaining) throws SerialStreamException, IOException {
    int remaining = frameBuffer.remaining();
    if (remaining < expectedRemaining) {
      refillBuffer(expectedRemaining - remaining);
    }
  }

  private void refillBuffer(int refillSize) throws SerialStreamException, IOException {
    frameBuffer.mark();
    frameBuffer.limit(frameBuffer.position() + refillSize);
    int refilled = 0;
    long timeOutPoint = System.currentTimeMillis();
    while (refilled < refillSize) {
      refilled += serialConnection.readData(frameBuffer);
      if (timeOutPoint < System.currentTimeMillis()) {
        frameBuffer.limit(frameBuffer.position());
        frameBuffer.reset();
        throw new SerialStreamException("Frame complete timeout!");
      }
    }
    frameBuffer.reset();
  }

  private void purgeAndLoadBuffer() throws SerialStreamException, IOException {
    frameBuffer.position(0);
    frameBuffer.limit(MAX_Z_WAVE_FRAME_SIZE);
    serialConnection.readData(frameBuffer);
    frameBuffer.limit(frameBuffer.position());
    frameBuffer.position(0);
  }
}
