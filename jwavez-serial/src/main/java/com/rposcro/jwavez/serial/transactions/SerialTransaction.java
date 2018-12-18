package com.rposcro.jwavez.serial.transactions;

import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import java.util.Optional;

public interface SerialTransaction<T> {

  TransactionStatus status();
  boolean expectsCallbacks();
  boolean expectsResponse();

  TransactionContext init(TransactionId transactionId);
  SOFRequestFrame startUp();
  Optional<SOFFrame> acceptInboundFrame(SOFFrame inboundFrame);
  void deliverySuccessful();
  void deliveryFailed();
  void timeoutOccurred();
}
