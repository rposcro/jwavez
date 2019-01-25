package com.rposcro.jwavez.serial.probe.transactions;

import com.rposcro.jwavez.serial.probe.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.probe.frame.SOFFrame;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * @param <T> Transaction callback type
 */
@Slf4j
public class RequestCallbackFlowTransaction<T> extends AbstractSerialTransaction<T> {

  private Function<Byte, SOFRequestFrame> startFrameProvider;
  private Class<? extends SOFCallbackFrame> expectedCallbackFrameClass;

  private Phase phase;
  private boolean deliveryConfirmed;

  public RequestCallbackFlowTransaction(Function<Byte, SOFRequestFrame> startFrameProvider, Class<? extends  SOFCallbackFrame> expectedCallbackFrameClass) {
    super(true, false);
    this.startFrameProvider = startFrameProvider;
    this.expectedCallbackFrameClass = expectedCallbackFrameClass;
  }

  public RequestCallbackFlowTransaction(SOFRequestFrame requestFrame, Class<? extends  SOFCallbackFrame> expectedCallbackFrameClass) {
    super(true, false);
    this.startFrameProvider = id -> requestFrame;
    this.expectedCallbackFrameClass = expectedCallbackFrameClass;
  }

  @Override
  public SOFRequestFrame startUp() {
    setPhase(Phase.REQUEST_ORDERED);
    deliveryConfirmed = false;
    return this.startFrameProvider.apply(transactionContext.getTransactionId().getCallbackId());
  }

  @Override
  public Optional<SOFFrame> acceptInboundFrame(SOFFrame frame) {
    if (!deliveryConfirmed) {
      log.warn("Received frame while request delivery hasn't been confirmed yet");
    }

    if (phase == Phase.REQUEST_CONFIRMED) {
      if (expectedCallbackFrameClass != frame.getClass()) {
        log.warn("Received frame class {} doesn't match expected callback class {}", frame.getClass(), expectedCallbackFrameClass);
        failTransaction();
      } else {
        completeTransaction((T) frame);
      }
    } else {
      log.warn("Received frame at wrong phase {}", phase);
    }

    setPhase(Phase.END);
    return Optional.empty();
  }

  @Override
  public void deliverySuccessful() {
    if (deliveryConfirmed) {
      log.warn("Received delivery confirmation while it has already been confirmed");
    }
    switch(phase) {
      case REQUEST_ORDERED:
        deliveryConfirmed = true;
        setPhase(Phase.REQUEST_CONFIRMED);
        break;
    }
  }

  @Override
  public void deliveryFailed() {
    if (deliveryConfirmed) {
      log.warn("Received send failure while it has already been confirmed");
    }
    deliveryConfirmed = true;
    setPhase(Phase.END);
    failTransaction();
  }

  @Override
  public Optional<SOFFrame> timeoutOccurred() {
    setPhase(Phase.END);
    failTransaction();
    return Optional.empty();
  }

  private void setPhase(Phase phase) {
    this.phase = phase;
    log.debug("Phase changed to " + phase);
  }

  private enum Phase {
    REQUEST_ORDERED,
    REQUEST_CONFIRMED,
    END,
  }
}
