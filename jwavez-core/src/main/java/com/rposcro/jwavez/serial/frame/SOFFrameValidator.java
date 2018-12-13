package com.rposcro.jwavez.serial.frame;

import static com.rposcro.jwavez.serial.frame.SOFFrame.*;

import com.rposcro.jwavez.serial.frame.contants.FrameCategory;
import com.rposcro.jwavez.serial.utils.ByteBuffer;
import com.rposcro.jwavez.serial.utils.FrameUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SOFFrameValidator {

  public boolean validate(ByteBuffer buffer) {
    return validateFrameLength(buffer)
        && validateFrameCategory(buffer)
        && validatePayloadSize(buffer)
        && validateFrameCRC(buffer)
        ;
  }

  private boolean validateFrameLength(ByteBuffer buffer) {
    if (buffer.getLength() < 5) {
      log.info("Wrong frame length {}", buffer.getLength());
      return false;
    }
    return true;
  }

  private boolean validateFrameCategory(ByteBuffer buffer) {
    if (FrameCategory.SOF.getCode() != buffer.get(OFFSET_CATEGORY)) {
      log.info("Wrong frame category, expected SOF frame but received {}", buffer.get(OFFSET_CATEGORY));
      return false;
    }
    return true;
  }

  private boolean validatePayloadSize(ByteBuffer buffer) {
    if (buffer.getLength() != Byte.toUnsignedInt(buffer.get(OFFSET_LENGTH)) + 2) {
      log.info("Wrong payload size {}", Byte.toUnsignedInt(buffer.get(OFFSET_LENGTH)));
      return false;
    }
    return true;
  }

  private boolean validateFrameCRC(ByteBuffer buffer) {
    byte calculatedCRC = FrameUtil.frameCRC(buffer);
    byte receivedCRC = buffer.get(buffer.getLength() - 1);
    if (receivedCRC != calculatedCRC) {
      log.info("Incorrect CRC {}!={}", receivedCRC, calculatedCRC);
      return false;
    }
    return true;
  }
}
