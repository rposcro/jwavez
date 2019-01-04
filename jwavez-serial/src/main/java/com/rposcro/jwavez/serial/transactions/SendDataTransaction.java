package com.rposcro.jwavez.serial.transactions;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.frame.callbacks.SendDataCallbackFrame;
import com.rposcro.jwavez.serial.frame.constants.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.frame.requests.SendDataRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.SendDataResponseFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendDataTransaction extends AbstractRequestResponseCallbackTransaction<SendDataResponseFrame, SendDataCallbackFrame, Void> {

  private NodeId nodeId;
  private ZWaveControlledCommand command;

  public SendDataTransaction(NodeId nodeId, ZWaveControlledCommand command) {
    this.nodeId = nodeId;
    this.command = command;
  }

  @Override
  protected SendDataRequestFrame startUpFrame() {
    return new SendDataRequestFrame(nodeId, command, transactionContext.getTransactionId().getCallbackId());
  }

  @Override
  protected boolean handleResponse(SendDataResponseFrame responseFrame) {
    if (!responseFrame.isRequestAccepted()) {
      failTransaction();
      return false;
    }
    return true;
  }

  @Override
  protected void handleCallback(SendDataCallbackFrame callbackFrame) {
    TransmitCompletionStatus status = callbackFrame.getTxStatus();
    if (status == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
      completeTransaction(null);
    } else if (status == TransmitCompletionStatus.TRANSMIT_COMPLETE_NO_ACK) {
      // special weak up treatment needed here, now just failing
      log.warn("Unsupported NO_ACK situation, needs to be implemented");
      failTransaction();
    } else {
      failTransaction();
    }
  }
}
