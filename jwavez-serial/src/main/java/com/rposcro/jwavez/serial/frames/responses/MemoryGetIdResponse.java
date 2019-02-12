package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.MEMORY_GET_ID)
public class MemoryGetIdResponse extends ZWaveResponse {

  private long homeId;
  private NodeId nodeId;

  public MemoryGetIdResponse(ViewBuffer frameBuffer) {
    super(frameBuffer);
    frameBuffer.position(FRAME_OFFSET_PAYLOAD);
    this.homeId = frameBuffer.getUnsignedDWord();
    this.nodeId = new NodeId(frameBuffer.get());
  }
}
