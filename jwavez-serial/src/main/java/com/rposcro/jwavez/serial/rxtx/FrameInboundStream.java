package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_NAK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_SOF;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_LENGTH;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.MAX_Z_WAVE_FRAME_SIZE;

import com.rposcro.jwavez.serial.exceptions.FrameTimeoutException;
import com.rposcro.jwavez.serial.exceptions.OddFrameException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import com.rposcro.jwavez.serial.utils.ViewBuffer;
import java.nio.ByteBuffer;
import lombok.Builder;

public class FrameInboundStream {

  private SerialPort serialPort;
  private RxTxConfiguration configuration;

  private final ByteBuffer frameBuffer;
  private final ViewBuffer viewBuffer;

  @Builder
  public FrameInboundStream(SerialPort serialPort, RxTxConfiguration configuration) {
    this();
    this.serialPort = serialPort;
    this.configuration = configuration;
  }

  private FrameInboundStream() {
    this.frameBuffer = ByteBuffer.allocateDirect(MAX_Z_WAVE_FRAME_SIZE * 2);
    this.frameBuffer.limit(0);
    this.viewBuffer = new ViewBuffer(frameBuffer);
  }

  public ViewBuffer nextFrame() throws SerialException {
    if (!frameBuffer.hasRemaining()) {
      purgeAndLoadBuffer();
    }

    if (frameBuffer.hasRemaining()) {
      setViewOverFrame();
      progressBuffer();
    } else {
      setViewOverEmpty();
    }

    return viewBuffer;
  }

  public void purgeStream() throws SerialPortException {
    do {
      frameBuffer.position(0).limit(frameBuffer.capacity());
    } while (serialPort.readData(frameBuffer) > 0);
    frameBuffer.position(0).limit(0);
  }

  private void purgeAndLoadBuffer() throws SerialPortException {
    frameBuffer.position(0);
    frameBuffer.limit(MAX_Z_WAVE_FRAME_SIZE);
    serialPort.readData(frameBuffer);
    frameBuffer.limit(frameBuffer.position());
    frameBuffer.position(0);
  }

  private void progressBuffer() {
    frameBuffer.position(frameBuffer.position() + viewBuffer.length());
  }

  private ViewBuffer setViewOverEmpty() {
    viewBuffer.setViewRange(0, 0);
    return viewBuffer;
  }

  private ViewBuffer setViewOverFrame() throws SerialException {
    int position = frameBuffer.position();
    byte category = frameBuffer.get(position);

    if (category == CATEGORY_ACK || category == CATEGORY_CAN || category == CATEGORY_NAK ) {
      viewBuffer.setViewRange(position, 1);
    } else if (category == CATEGORY_SOF) {
      return setViewOverSOF();
    } else {
      throw new OddFrameException("Unrecognized frame category %02x", category);
    }
    return viewBuffer;
  }

  private ViewBuffer setViewOverSOF() throws SerialException {
    int position = frameBuffer.position();
    ensureRemaining(3);
    int length = frameBuffer.get(position + FRAME_OFFSET_LENGTH) + 2;
    ensureRemaining(length);
    viewBuffer.setViewRange(position, length);
    return viewBuffer;
  }

  private void ensureRemaining(int expectedRemaining) throws SerialException {
    int remaining = frameBuffer.remaining();
    if (remaining < expectedRemaining) {
      refillBuffer(expectedRemaining - remaining);
    }
  }

  private void refillBuffer(int refillSize) throws SerialException {
    frameBuffer.mark();
    frameBuffer.position(frameBuffer.limit());
    frameBuffer.limit(frameBuffer.limit() + refillSize);
    int refilled = 0;
    long timeOutPoint = System.currentTimeMillis() + configuration.getFrameCompleteTimeout();
    while (refilled < refillSize) {
      refilled += serialPort.readData(frameBuffer);
      if (timeOutPoint < System.currentTimeMillis()) {
        frameBuffer.limit(frameBuffer.position());
        frameBuffer.reset();
        throw new FrameTimeoutException("Frame complete timeout!");
      }
    }
    frameBuffer.reset();
  }
}
