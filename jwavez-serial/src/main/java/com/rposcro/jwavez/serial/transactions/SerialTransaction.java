package com.rposcro.jwavez.serial.transactions;

import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
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
