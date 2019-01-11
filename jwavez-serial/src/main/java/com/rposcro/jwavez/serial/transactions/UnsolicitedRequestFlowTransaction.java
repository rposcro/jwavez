package com.rposcro.jwavez.serial.transactions;

import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnsolicitedRequestFlowTransaction extends AbstractSerialTransaction<Void> {

  private Supplier<SOFRequestFrame> startFrameSupplier;

  public UnsolicitedRequestFlowTransaction(Supplier<SOFRequestFrame> startFrameSupplier) {
    super(false, false);
    this.startFrameSupplier = startFrameSupplier;
  }

  public UnsolicitedRequestFlowTransaction(SOFRequestFrame startFrame) {
    super(false, false);
    this.startFrameSupplier = () -> startFrame;
  }

  @Override
  public SOFRequestFrame startUp() {
    return this.startFrameSupplier.get();
  }

  @Override
  public Optional<SOFFrame> acceptInboundFrame(SOFFrame frame) {
    log.warn("Unsolicited transaction doens't expect inbound frames!");
    return Optional.empty();
  }

  @Override
  public void deliverySuccessful() {
    completeTransaction(null);
  }

  @Override
  public void deliveryFailed() {
    failTransaction();
  }

  @Override
  public Optional<SOFFrame> timeoutOccurred() {
    failTransaction();
    return Optional.empty();
  }
}
