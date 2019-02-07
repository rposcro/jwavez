package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.RxStatus;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.APPLICATION_COMMAND_HANDLER)
public class ApplicationCommandHandlerCallback extends Callback {

  private RxStatus rxStatus;
  private NodeId sourceNodeId;
  private int commandLength;
  private byte[] commandPayload;

  public ApplicationCommandHandlerCallback(ViewBuffer frameBuffer) {
    super(frameBuffer);
    frameBuffer.position(FRAME_OFFSET_PAYLOAD);
    this.rxStatus = new RxStatus(frameBuffer.get());
    this.sourceNodeId = new NodeId(frameBuffer.get());
    this.commandLength = frameBuffer.get() & 0xFF;
    this.commandPayload = frameBuffer.getBytes(commandLength);
  }
}
