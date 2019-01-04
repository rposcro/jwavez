package com.rposcro.jwavez.serial.builders;

import static com.rposcro.jwavez.serial.frame.SOFFrame.*;

import com.rposcro.jwavez.serial.frame.constants.FrameCategory;
import com.rposcro.jwavez.serial.frame.constants.FrameType;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.utils.FrameUtil;

public class FrameDataBuilder {

  private byte[] buffer;
  private int payloadOffset = OFFSET_PAYLOAD;

  public FrameDataBuilder(int bufferLength) {
    buffer = new byte[bufferLength];
    buffer[OFFSET_CATEGORY] = FrameCategory.SOF.getCode();
    buffer[OFFSET_LENGTH] = (byte) (bufferLength - 2);
  }

  public FrameDataBuilder serialCommand(SerialCommand serialCommand) {
    buffer[OFFSET_COMMAND] = serialCommand.getCode();
    return this;
  }

  public FrameDataBuilder frameType(FrameType frameType) {
    buffer[OFFSET_FRAME_TYPE] = frameType.getCode();
    return this;
  }

  public FrameDataBuilder withByte(byte chunk) {
    buffer[payloadOffset++] = chunk;
    return this;
  }

  public FrameDataBuilder withBytes(byte... chunk) {
    System.arraycopy(chunk, 0, buffer, payloadOffset, chunk.length);
    payloadOffset += chunk.length;
    return this;
  }

  public byte[] buildData() {
    buffer[buffer.length - 1] = FrameUtil.frameCRC(buffer);
    return buffer;
  }
}
