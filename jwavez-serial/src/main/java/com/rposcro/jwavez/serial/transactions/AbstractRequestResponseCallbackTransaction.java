package com.rposcro.jwavez.serial.transactions;

import static com.rposcro.jwavez.serial.frame.SOFFrame.OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.exceptions.TransactionException;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.frame.constants.FrameType;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import java.util.Optional;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRequestResponseCallbackTransaction<RT extends SOFResponseFrame, CT extends SOFCallbackFrame, CTX> extends AbstractSerialTransaction<CTX> {

  private SerialCommand serialCommand;
  private byte callbackId;

  private Phase phase;
  private boolean deliveryConfirmed;

  public AbstractRequestResponseCallbackTransaction(SerialCommand serialCommand) {
    super(true, true);
    this.serialCommand = serialCommand;
  }

  protected abstract SOFRequestFrame startUpFrame();

  protected abstract boolean handleResponse(RT responseFrame);

  protected abstract void handleCallback(CT responseFrame);

  @Override
  public Future<TransactionResult<CTX>> init(TransactionContext transactionContext) {
    callbackId = transactionContext.getTransactionId().getCallbackId();
    phase = Phase.IDLE;
    return super.init(transactionContext);
  }

  @Override
  public SOFRequestFrame startUp() {
    setPhase(Phase.REQUEST_SEND);
    deliveryConfirmed = false;
    return startUpFrame();
  }

  @Override
  public Optional<SOFFrame> acceptInboundFrame(SOFFrame inboundFrame) {
    try {
      validateFrameReceiptReadiness();

      if (phase == Phase.REQUEST_SEND) {
        RT responseFrame = validateResponseAndCast(inboundFrame);
        if (!handleResponse(responseFrame)) {
          setPhase(Phase.END);
        } else {
          setPhase(Phase.RESPONSE_RECEIVED);
        }
      } else if (phase == Phase.RESPONSE_RECEIVED) {
        CT callbackFrame = validateCallbackAndCast(inboundFrame);
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
  public Optional<SOFFrame> timeoutOccurred() {
    setPhase(Phase.END);
    failTransaction();
    return Optional.empty();
  }

  private void validateFrameReceiptReadiness() throws TransactionException {
    if (!deliveryConfirmed) {
      throw new TransactionException("Received frame but prior hasn't been confirmed yet, stopping");
    }
    if (phase == Phase.IDLE || phase == Phase.END) {
      throw new TransactionException("Frames are not expected at phase " + phase);
    }
  }

  protected <T extends SOFCallbackFrame> T validateCallbackAndCast(SOFFrame frame) throws TransactionException {
    if (frame.getFrameType() == FrameType.REQ
        && frame.getSerialCommand() == serialCommand
        && frame.getBuffer()[OFFSET_PAYLOAD] == callbackId) {
      try {
        return (T) frame;
      } catch(ClassCastException e) {
        throw new TransactionException("Callback frame validation failed, stopping");
      }
    }
    throw new TransactionException("Callback frame validation failed, stopping");
  }

  protected <T extends SOFResponseFrame> T validateResponseAndCast(SOFFrame frame) throws TransactionException {
    if (frame.getFrameType() == FrameType.RES
        && frame.getSerialCommand() == serialCommand) {
      try {
        return (T) frame;
      } catch(ClassCastException e) {
        throw new TransactionException("Response frame validation failed, stopping");
      }
    }
    throw new TransactionException("Response frame validation failed, stopping");
  }

  protected void stopTransaction() {
    setPhase(Phase.END);
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
