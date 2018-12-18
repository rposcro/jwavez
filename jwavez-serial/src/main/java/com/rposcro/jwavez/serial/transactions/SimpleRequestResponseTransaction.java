package com.rposcro.jwavez.serial.transactions;

import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleRequestResponseTransaction<T extends SOFResponseFrame> extends AbstractSerialTransaction<T> {

  private SOFRequestFrame requestFrame;
  private Class<T> expectedResponseFrameClass;

  private Phase phase;
  private boolean deliveryConfirmed;

  public SimpleRequestResponseTransaction(SOFRequestFrame requestFrame, Class<T> expectedResponseFrameClass) {
    super(false, true);
    this.requestFrame = requestFrame;
    this.expectedResponseFrameClass = expectedResponseFrameClass;
  }

  @Override
  public SOFRequestFrame startUp() {
    setPhase(Phase.REQUEST_ORDERED);
    deliveryConfirmed = false;
    return this.requestFrame;
  }

  @Override
  public Optional<SOFFrame> acceptInboundFrame(SOFFrame frame) {
    if (!deliveryConfirmed) {
      log.warn("Received frame while request delivery hasn't been confirmed yet");
    }

    if (phase == Phase.REQUEST_CONFIRMED) {
      if (expectedResponseFrameClass != frame.getClass()) {
        log.warn("Received frame class {} doesn't match expected response class {}", frame.getClass(), expectedResponseFrameClass);
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
  public void timeoutOccurred() {
    setPhase(Phase.END);
    failTransaction();
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
