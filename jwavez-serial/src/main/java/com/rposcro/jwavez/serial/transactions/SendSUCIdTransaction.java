package com.rposcro.jwavez.serial.transactions;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.frame.callbacks.SendSUCIdCallbackFrame;
import com.rposcro.jwavez.serial.frame.constants.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.frame.requests.SendSUCIdRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.SendSUCIdResponseFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendSUCIdTransaction extends AbstractRequestResponseCallbackTransaction<SendSUCIdResponseFrame, SendSUCIdCallbackFrame, Void> {

  public SendSUCIdTransaction(NodeId addresseeId) {
    super(new SendSUCIdRequestFrame(addresseeId));
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
