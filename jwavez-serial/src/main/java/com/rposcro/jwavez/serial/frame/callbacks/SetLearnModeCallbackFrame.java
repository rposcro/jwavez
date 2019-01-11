package com.rposcro.jwavez.serial.frame.callbacks;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.frame.CallbackFrameModel;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.constants.LearnStatus;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@CallbackFrameModel(function = SerialCommand.SET_LEARN_MODE)
public class SetLearnModeCallbackFrame extends SOFCallbackFrame {

  private static final int OFFSET_TRANSMIT_STATUS = OFFSET_FUNC_ID + 1;
  private static final int OFFSET_NODE_ID = OFFSET_FUNC_ID + 2;

  private LearnStatus learnStatus;
  private NodeId nodeId;

  public SetLearnModeCallbackFrame(byte[] buffer) {
    super(buffer);
    this.learnStatus = LearnStatus.ofCode(buffer[OFFSET_TRANSMIT_STATUS]);
    this.nodeId = new NodeId(buffer[OFFSET_NODE_ID]);
  }
}
