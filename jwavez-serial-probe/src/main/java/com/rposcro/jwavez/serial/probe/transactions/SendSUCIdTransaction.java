package com.rposcro.jwavez.serial.probe.transactions;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.callbacks.SendSUCIdCallbackFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.constants.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.probe.frame.requests.SendSUCIdRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.SendSUCIdResponseFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendSUCIdTransaction extends AbstractRequestResponseCallbackTransaction<SendSUCIdResponseFrame, SendSUCIdCallbackFrame, Void> {

  private NodeId addreseeId;

  public SendSUCIdTransaction(NodeId addresseeId) {
    super(SerialCommand.SEND_SUC_ID);
    this.addreseeId = addresseeId;
  }

  @Override
  protected SOFRequestFrame startUpFrame() {
    return new SendSUCIdRequestFrame(addreseeId, transactionContext.getTransactionId().getCallbackId());
  }

  @Override
  protected boolean handleResponse(SendSUCIdResponseFrame responseFrame) {
    if (!responseFrame.isRequestAccepted()) {
      failTransaction();
      return false;
    }
    return true;
  }

  @Override
  protected void handleCallback(SendSUCIdCallbackFrame callbackFrame) {
    if (callbackFrame.getTxStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
      completeTransaction(null);
    } else {
      failTransaction();
    }
  }
}
