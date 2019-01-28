package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_NAK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.MAX_Z_WAVE_FRAME_SIZE;

import java.io.IOException;
import java.nio.ByteBuffer;
import lombok.Builder;

public class FrameOutboundStream {

  private final ByteBuffer sharedBuffer;
  private final ByteBuffer ackBuffer;
  private final ByteBuffer nakBuffer;
  private final ByteBuffer canBuffer;
  private final ByteBuffer sofBuffer;

  private SerialConnection serialConnection;

  @Builder
  public FrameOutboundStream(SerialConnection serialConnection) {
    this();
    this.serialConnection = serialConnection;
  }

  private FrameOutboundStream() {
    this.sharedBuffer = ByteBuffer.allocateDirect(MAX_Z_WAVE_FRAME_SIZE + 3)
        .put(CATEGORY_ACK)
        .put(CATEGORY_NAK)
        .put(CATEGORY_CAN);
    this.ackBuffer = (ByteBuffer) ((ByteBuffer) (sharedBuffer.position(0).limit(1))).slice().mark();
    this.nakBuffer = (ByteBuffer) ((ByteBuffer) (sharedBuffer.position(1).limit(2))).slice().mark();
    this.canBuffer = (ByteBuffer) ((ByteBuffer) (sharedBuffer.position(2).limit(3))).slice().mark();
    this.sofBuffer = (ByteBuffer) ((ByteBuffer) (sharedBuffer.position(3).limit(sharedBuffer.capacity()))).slice().mark();
  }

  public void writeCAN() throws IOException {
    canBuffer.reset();
    serialConnection.writeData(canBuffer);
  }

  public void writeNAK() throws IOException {
    nakBuffer.reset();
    serialConnection.writeData(nakBuffer);
  }

  public void writeACK() throws IOException {
    ackBuffer.reset();
    serialConnection.writeData(ackBuffer);
  }

  public void writeSOF(ByteBuffer sofBuffer) throws IOException {
    serialConnection.writeData(sofBuffer);
  }
}
