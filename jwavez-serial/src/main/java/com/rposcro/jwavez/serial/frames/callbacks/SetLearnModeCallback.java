package com.rposcro.jwavez.serial.frames.callbacks;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.LearnStatus;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.SET_LEARN_MODE)
public class SetLearnModeCallback extends FlowCallback {

  private LearnStatus learnStatus;
  private NodeId nodeId;

  public SetLearnModeCallback(ViewBuffer frameBuffer) {
    super(frameBuffer);
    this.learnStatus = LearnStatus.ofCode(frameBuffer.get());
    this.nodeId = new NodeId(frameBuffer.get());
  }
}
