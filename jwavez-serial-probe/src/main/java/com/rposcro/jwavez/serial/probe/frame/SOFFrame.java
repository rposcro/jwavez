package com.rposcro.jwavez.serial.probe.frame;

import com.rposcro.jwavez.serial.probe.frame.constants.FrameCategory;
import com.rposcro.jwavez.serial.probe.frame.constants.FrameType;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.utils.FrameUtil;

public abstract class SOFFrame extends SerialFrame {

  public final static int OFFSET_CATEGORY = 0;
  public final static int OFFSET_LENGTH = 1;
  public final static int OFFSET_FRAME_TYPE = 2;
  public final static int OFFSET_COMMAND = 3;
  public final static int OFFSET_PAYLOAD = 4;

  private FrameType frameType;
  private SerialCommand serialCommand;

  protected SOFFrame(FrameType sofFrameType, SerialCommand serialCommand) {
    this.frameType = sofFrameType;
    this.serialCommand = serialCommand;
    byte[] buffer = new byte[5];
    buffer[0] = FrameCategory.SOF.getCode();
    buffer[1] = 3;
    buffer[2] = sofFrameType.getCode();
    buffer[3] = serialCommand.getCode();
    buffer[4] = FrameUtil.frameCRC(buffer);
    super.setBuffer(buffer);
  }

  protected SOFFrame(FrameType frameType, SerialCommand serialCommand, int payloadSize, int payloadOffset, byte[] payload) {
    this.frameType = frameType;
    this.serialCommand = serialCommand;
    byte[] buffer = new byte[payloadSize + 5];
    buffer[0] = FrameCategory.SOF.getCode();
    buffer[1] = (byte) (payloadSize + 3);
    buffer[2] = frameType.getCode();
    buffer[3] = serialCommand.getCode();
    System.arraycopy(payload, payloadOffset, buffer, 4, payloadSize);
    buffer[payloadSize + 4] = FrameUtil.frameCRC(buffer);
    super.setBuffer(buffer);
  }

  protected SOFFrame(byte[] buffer) {
    super.setBuffer(buffer);
    this.frameType = FrameType.ofCode(buffer[OFFSET_FRAME_TYPE]);
    this.serialCommand = SerialCommand.ofCode(buffer[OFFSET_COMMAND]);
  }

  protected SOFFrame(int dataSize, int dataOffset, byte[] buffer) {
    byte[] bufferCopy = new byte[dataSize];
    System.arraycopy(buffer, dataOffset, bufferCopy, 0, dataSize);
    super.setBuffer(bufferCopy);
    this.frameType = FrameType.ofCode(bufferCopy[OFFSET_FRAME_TYPE]);
    this.serialCommand = SerialCommand.ofCode(bufferCopy[OFFSET_COMMAND]);
  }

  public FrameType getFrameType() {
    return this.frameType;
  }

  public SerialCommand getSerialCommand() {
    return this.serialCommand;
  }
}
