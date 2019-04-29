package com.rposcro.jwavez.serial.probe.transactions;

import com.rposcro.jwavez.serial.probe.frame.SOFFrame;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import java.util.Optional;
import java.util.concurrent.Future;

public interface SerialTransaction<T> {

  TransactionStatus status();
  boolean expectsCallbacks();
  boolean expectsResponse();

  Future<TransactionResult<T>> init(TransactionContext context);
  SOFRequestFrame startUp();
  Optional<SOFFrame> acceptInboundFrame(SOFFrame inboundFrame);
  Optional<SOFFrame> timeoutOccurred();
  void deliverySuccessful();
  void deliveryFailed();
}
