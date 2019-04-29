package com.rposcro.jwavez.serial.probe.transactions;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.frame.callbacks.SendDataCallbackFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.constants.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.probe.frame.requests.SendDataRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.SendDataResponseFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendDataTransaction extends AbstractRequestResponseCallbackTransaction<SendDataResponseFrame, SendDataCallbackFrame, Void> {

  private NodeId nodeId;
  private ZWaveControlledCommand command;
  private boolean ackRequired;

  public SendDataTransaction(NodeId nodeId, ZWaveControlledCommand command) {
    this(nodeId, command, true);
  }

  public SendDataTransaction(NodeId nodeId, ZWaveControlledCommand command, boolean ackRequired) {
    super(SerialCommand.SEND_DATA);
    this.nodeId = nodeId;
    this.command = command;
    this.ackRequired = ackRequired;
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
      if (ackRequired) {
        // special weak up treatment needed here, now just failing
        log.warn("NO_ACK situation while ACK required, needs to be implemented");
        failTransaction();
      } else {
        log.warn("NO_ACK situation but ACK ignored");
        completeTransaction(null);
      }
    } else {
      failTransaction();
    }
  }
}
