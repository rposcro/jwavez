package com.rposcro.jwavez.serial.probe.frame.callbacks;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.frame.CallbackFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import java.util.Arrays;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@CallbackFrameModel(function = SerialCommand.APPLICATION_COMMAND_HANDLER)
public class ApplicationCommandHandlerCallbackFrame extends SOFCallbackFrame {

  private static final int OFFSET_RX_STATUS = OFFSET_PAYLOAD;
  private static final int OFFSET_SOURCE_NODE = OFFSET_PAYLOAD + 1;
  private static final int OFFSET_CMD_LENGTH = OFFSET_PAYLOAD + 2;
  private static final int OFFSET_CMD_DATA = OFFSET_PAYLOAD + 3;

  private NodeId sourceNodeId;
  private int commandLength;
  private byte[] commandPayload;

  public ApplicationCommandHandlerCallbackFrame(byte[] buffer) {
    super(buffer);
    this.sourceNodeId = new NodeId(buffer[OFFSET_SOURCE_NODE]);
    this.commandLength = buffer[OFFSET_CMD_LENGTH] & 0xFF;
    this.commandPayload = Arrays.copyOfRange(buffer, OFFSET_CMD_DATA, OFFSET_CMD_DATA + commandLength);
  }
}
