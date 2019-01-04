package com.rposcro.jwavez.serial.transactions;

import com.rposcro.jwavez.serial.exceptions.TransactionException;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRequestResponseCallbackTransaction<RT extends SOFResponseFrame, CT extends SOFCallbackFrame, CTX> extends AbstractSerialTransaction<CTX> {

  private SOFRequestFrame startUpFrame;
  private Phase phase;
  private boolean deliveryConfirmed;
  private byte callbackId;

  public AbstractRequestResponseCallbackTransaction(SOFRequestFrame startUpFrame) {
    super(true, true);
    this.startUpFrame = startUpFrame;
  }

  protected abstract boolean handleResponse(RT responseFrame);

  protected abstract void handleCallback(CT responseFrame);

  @Override
  public TransactionContext<CTX> init(TransactionId transactionId) {
    super.init(transactionId);
    callbackId = transactionContext.getTransactionId().getCallbackId();
    phase = Phase.IDLE;
    return transactionContext;
  }

  @Override
  public SOFRequestFrame startUp() {
    setPhase(Phase.REQUEST_SEND);
    deliveryConfirmed = false;
    return startUpFrame;
  }

  @Override
  public Optional<SOFFrame> acceptInboundFrame(SOFFrame inboundFrame) {
    try {
      validateFrameReceivalReadiness();

      if (phase == Phase.REQUEST_SEND) {
        RT responseFrame = validateResponseAndCast(inboundFrame);
        if (!handleResponse(responseFrame)) {
          setPhase(Phase.END);
        } else {
          setPhase(Phase.RESPONSE_RECEIVED);
        }
      } else if (phase == Phase.RESPONSE_RECEIVED) {
        CT callbackFrame = validateCallbackAndCast(inboundFrame, callbackId);
        handleCallback(callbackFrame);
        setPhase(Phase.END);
      }
    } catch(TransactionException e) {
      log.error(e.getMessage());
      setPhase(Phase.END);
      failTransaction();
    }

    return Optional.empty();
  }

  @Override
  public void deliverySuccessful() {
    if (deliveryConfirmed) {
      log.warn("Received order delivery confirmation while it has already been confirmed");
    }
    deliveryConfirmed = true;
  }

  @Override
  public void deliveryFailed() {
    if (deliveryConfirmed) {
      log.warn("Received order delivery failure while it has already been confirmed");
    }
    setPhase(Phase.END);
    failTransaction();
  }

  @Override
  public void timeoutOccurred() {
    setPhase(Phase.END);
    failTransaction();
  }

  private void validateFrameReceivalReadiness() throws TransactionException {
    if (!deliveryConfirmed) {
      throw new TransactionException("Received frame but prior hasn't been confirmed yet, stopping");
    }
    if (phase == Phase.IDLE || phase == Phase.END) {
      throw new TransactionException("Frames are not expected at phase " + phase);
    }
  }

  private void setPhase(Phase phase) {
    this.phase = phase;
    log.debug("Phase changed to " + phase);
  }

  private enum Phase {
    IDLE,
    REQUEST_SEND,
    RESPONSE_RECEIVED,
    END,
  }
}
