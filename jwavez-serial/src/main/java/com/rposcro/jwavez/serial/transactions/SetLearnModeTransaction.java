package com.rposcro.jwavez.serial.transactions;

import static com.rposcro.jwavez.serial.frame.SOFFrame.OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.TransactionException;
import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.callbacks.SetLearnModeCallbackFrame;
import com.rposcro.jwavez.serial.frame.constants.FrameType;
import com.rposcro.jwavez.serial.frame.constants.LearnMode;
import com.rposcro.jwavez.serial.frame.constants.LearnStatus;
import com.rposcro.jwavez.serial.frame.requests.SetLearnModeRequestFrame;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetLearnModeTransaction extends AbstractSerialTransaction<NodeId> {

  private boolean deliveryConfirmed;
  private Phase phase;

  public SetLearnModeTransaction() {
    super(true, false);
    setPhase(Phase.IDLE);
  }

  @Override
  public SetLearnModeRequestFrame startUp() {
    setPhase(Phase.LEARN_MODE_ACTIVATING);
    deliveryConfirmed = false;
    return new SetLearnModeRequestFrame(LearnMode.LEARN_MODE_CLASSIC, transactionContext.getCallbackId());
  }

  @Override
  public Optional<SOFFrame> acceptInboundFrame(SOFFrame frame) {
    validateCallback(frame);
    if (phase == Phase.LEARN_MODE_ACTIVATED) {
      receivedAtLearnActivated((SetLearnModeCallbackFrame) frame);
    } else if (phase == Phase.LEARN_MODE_STARTED) {
      receivedAtLearnStarted((SetLearnModeCallbackFrame) frame);
    }
    return Optional.empty();
  }

  private void receivedAtLearnActivated(SetLearnModeCallbackFrame frame) {
    if (frame.getLearnStatus() == LearnStatus.LEARN_STATUS_STARTED) {
      setPhase(Phase.LEARN_MODE_STARTED);
    } else {
      failTransaction();
      throw new TransactionException("Unexpected set learn callback status received in started phase: " + frame.getLearnStatus());
    }
  }

  private void receivedAtLearnStarted(SetLearnModeCallbackFrame frame) {
    if (frame.getLearnStatus() == LearnStatus.LEARN_STATUS_STARTED) {
      log.debug("Learning process continues ...");
    } else if (frame.getLearnStatus() == LearnStatus.LEARN_STATUS_DONE) {
      setPhase(Phase.END);
      completeTransaction(frame.getNodeId());
    }
  }

  @Override
  public void deliverySuccessful() {
    if (deliveryConfirmed) {
      log.warn("Received order delivery confirmation while it has already been confirmed");
      return;
    }

    switch (phase) {
      case LEARN_MODE_ACTIVATING:
        setPhase(Phase.LEARN_MODE_ACTIVATED);
        break;
      case LEARN_MODE_CANCELLING:
        setPhase(Phase.END);
        cancelTransaction();
        break;
      default:
        log.warn("Received order delivery confirmation in unexpected phase: " + phase);
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
    if (phase == Phase.LEARN_MODE_ACTIVATING || phase == Phase.LEARN_MODE_ACTIVATED) {
      setPhase(Phase.LEARN_MODE_CANCELLING);
      deliveryConfirmed = false;
      return Optional.of(disableLearnModeFrame());
    } else if (phase == Phase.LEARN_MODE_STARTED) {
      log.info("Transaction timeout occurred but process has already started, ignoring timeout");
    } else {
      setPhase(Phase.END);
      failTransaction();
      throw new TransactionException("Timeout in unexpected phase " + phase);
    }
    return Optional.empty();
  }

  private SetLearnModeRequestFrame disableLearnModeFrame() {
    return new SetLearnModeRequestFrame(LearnMode.LEARN_MODE_DISABLE, transactionContext.getCallbackId());
  }

  private void validateCallback(SOFFrame frame) throws TransactionException {
    if (frame instanceof SetLearnModeCallbackFrame
        && frame.getFrameType() == FrameType.REQ
        && frame.getBuffer()[OFFSET_PAYLOAD] == transactionContext.getCallbackId()) {
      return;
    }
    throw new TransactionException("Callback frame validation failed, stopping");
  }

  private void setPhase(Phase phase) {
    this.phase = phase;
    log.debug("Phase changed to " + phase);
  }

  private enum Phase {
    IDLE,
    LEARN_MODE_ACTIVATING,
    LEARN_MODE_ACTIVATED,
    LEARN_MODE_STARTED,
    LEARN_MODE_CANCELLING,
    END,
  }
}
