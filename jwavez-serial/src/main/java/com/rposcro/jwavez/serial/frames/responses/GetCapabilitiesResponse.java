package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_CAPABILITIES)
public class GetCapabilitiesResponse extends Response {

  private byte serialAppVersion;
  private byte serialAppRevision;
  private int manufacturerId;
  private int manufacturerProductType;
  private int manufacturerProductId;
  List<Integer> serialCommands;

  public GetCapabilitiesResponse(ViewBuffer frameBuffer) {
    super(frameBuffer);
    frameBuffer.position(FRAME_OFFSET_PAYLOAD);
    this.serialAppVersion = frameBuffer.get();
    this.serialAppRevision = frameBuffer.get();
    this.manufacturerId = frameBuffer.getUnsignedWord();
    this.manufacturerProductType = frameBuffer.getUnsignedWord();
    this.manufacturerProductId = frameBuffer.getUnsignedWord();
    this.serialCommands = parseSerialCommandsMask(frameBuffer);
  }

  public List<Integer> parseSerialCommandsMask(ViewBuffer frameBuffer) {
    List<Integer> functions = new LinkedList<>();
    int functionId = 1;
    while (frameBuffer.remaining() > 1) {
      byte chunk = frameBuffer.get();
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
