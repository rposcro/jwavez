package com.rposcro.jwavez.serial.probe.transactions;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.frame.callbacks.SetSUCNodeIdCallbackFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.requests.SetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.SetSUCNodeIdResponseFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetSUCNodeIdTransaction extends AbstractRequestResponseCallbackTransaction<SetSUCNodeIdResponseFrame, SetSUCNodeIdCallbackFrame, Void> {

  private NodeId sucNodeId;
  private boolean localController;
  private boolean enableSucAndSis;

  public SetSUCNodeIdTransaction(NodeId sucNodeId, boolean enableSucAndSis, boolean localController) {
    super(SerialCommand.SET_SUC_NODE_ID);
    this.sucNodeId = sucNodeId;
    this.localController = localController;
    this.enableSucAndSis = enableSucAndSis;
  }

  @Override
  public SetSUCNodeIdRequestFrame startUpFrame() {
    return localController ?
        new SetSUCNodeIdRequestFrame(sucNodeId, enableSucAndSis) :
        new SetSUCNodeIdRequestFrame(sucNodeId, enableSucAndSis, transactionContext.getTransactionId().getCallbackId());
  }

  @Override
  protected boolean handleResponse(SetSUCNodeIdResponseFrame responseFrame) {
    if (!responseFrame.isRequestAccepted()) {
      failTransaction();
      return false;
    } else if (localController) {
      completeTransaction(null);
      return false;
    } else {
      return true;
    }
  }

  @Override
  protected void handleCallback(SetSUCNodeIdCallbackFrame callbackFrame) {
    if (callbackFrame.isSuccessful()) {
      completeTransaction(null);
    } else {
      failTransaction();
    }
  }
}
