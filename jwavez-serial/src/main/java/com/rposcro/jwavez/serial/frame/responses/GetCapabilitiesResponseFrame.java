package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_CAPABILITIES)
public class GetCapabilitiesResponseFrame extends SOFResponseFrame {

  private static final int OFFSET_VERSION = OFFSET_PAYLOAD;
  private static final int OFFSET_REVISION = OFFSET_PAYLOAD + 1;
  private static final int OFFSET_MANUFACTURER_ID = OFFSET_PAYLOAD + 2;
  private static final int OFFSET_PRODUCT_TYPE = OFFSET_PAYLOAD + 4;
  private static final int OFFSET_PRODUCT_ID = OFFSET_PAYLOAD + 6;
  private static final int OFFSET_FUNCTION_MASK = OFFSET_PAYLOAD + 8;

  private byte serialAppVersion;
  private byte serialAppRevision;
  private int manufacturerId;
  private int manufacturerProductType;
  private int manufacturerProductId;

  public GetCapabilitiesResponseFrame(byte[] buffer) {
    super(buffer);
    this.serialAppVersion = buffer[OFFSET_VERSION];
    this.serialAppRevision = buffer[OFFSET_REVISION];
    this.manufacturerId = readInt(buffer, OFFSET_MANUFACTURER_ID);
    this.manufacturerProductType = readInt(buffer, OFFSET_PRODUCT_TYPE);
    this.manufacturerProductId = readInt(buffer, OFFSET_PRODUCT_ID);
  }

  private int readInt(byte[] buffer, int offset) {
    return ((buffer[offset] & 0xff) << 8) | (buffer[offset + 1] & 0xFF);
  }

  public List<Integer> getFunctions() {
    List<Integer> functions = new LinkedList<>();
    byte[] buffer = getBuffer();
    int functionId = 1;
    for (int idx = OFFSET_FUNCTION_MASK; idx < buffer.length - 1; idx++) {
      byte chunk = buffer[idx];
      int bitMask = 1;
      for (int bit = 0; bit < 8; bit++) {
        if ((chunk & bitMask) > 0) {
          functions.add(functionId);
        }
        functionId++;
        bitMask <<= 1;
      }
    }
    return functions;
  }
}
