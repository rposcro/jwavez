package com.rposcro.jwavez.serial.frame.callbacks;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.frame.CallbackFrameModel;
import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.contants.ApplicationUpdateStatus;
import com.rposcro.jwavez.serial.utils.NodeUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@CallbackFrameModel(function = SerialCommand.APPLICATION_UPDATE)
public class ApplicationUpdateCallbackFrame extends SOFCallbackFrame {

  private ApplicationUpdateStatus status;
  private NodeInfo nodeInfo;

  public ApplicationUpdateCallbackFrame(byte[] buffer) {
    super(buffer);
    this.status = ApplicationUpdateStatus.ofCode(buffer[OFFSET_PAYLOAD]);
    if (status == ApplicationUpdateStatus.APP_UPDATE_STATUS_NODE_INFO_RECEIVED) {
      this.nodeInfo = NodeUtil.decodeNodeInfo(buffer, OFFSET_PAYLOAD + 1);
      log.debug("Received info of node {}", nodeInfo.getId());
    } else if (status == ApplicationUpdateStatus.APP_UPDATE_STATUS_NODE_INFO_REQ_FAILED) {
      log.info("Failed to obtain node info {}", status);
    } else {
      log.info("Unsupported application update status received {}", status);
    }
  }

  @Override
  public byte getCallbackFunctionId() {
    return 0;
  }
}
