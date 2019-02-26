package com.rposcro.jwavez.serial.frames;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_SOF;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_CATEGORY;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_LENGTH;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.utils.FrameUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InboundFrameValidator {

  private static InboundFrameValidator defaultInstance;

  public static InboundFrameValidator defaultValidator() {
    return defaultInstance == null ? defaultInstance = new InboundFrameValidator() : defaultInstance;
  }

  public boolean validate(ViewBuffer buffer) {
    return validateFrameLength(buffer)
        && validateFrameCategory(buffer)
        && validatePayloadSize(buffer)
        && validateFrameCRC(buffer)
        ;
  }

  private boolean validateFrameLength(ViewBuffer buffer) {
    if (buffer.length() < 5 ) {
      log.info("Wrong frame length {}", buffer.length());
      return false;
    }
    return true;
  }

  private boolean validateFrameCategory(ViewBuffer buffer) {
    if (CATEGORY_SOF != buffer.get(FRAME_OFFSET_CATEGORY)) {
      log.info("Wrong frame category, expected SOF frame but received {}", buffer.get(FRAME_OFFSET_CATEGORY));
      return false;
    }
    return true;
  }

  private boolean validatePayloadSize(ViewBuffer buffer) {
    if (buffer.length() != Byte.toUnsignedInt(buffer.get(FRAME_OFFSET_LENGTH)) + 2) {
      log.info("Wrong payload size {}", Byte.toUnsignedInt(buffer.get(FRAME_OFFSET_LENGTH)));
      return false;
    }
    return true;
  }

  private boolean validateFrameCRC(ViewBuffer buffer) {
    byte calculatedCRC = FrameUtil.frameCRC(buffer);
    byte receivedCRC = buffer.get(buffer.length() - 1);
    if (receivedCRC != calculatedCRC) {
      log.info("Incorrect CRC {}!={}", receivedCRC, calculatedCRC);
      return false;
    }
    return true;
  }
}
