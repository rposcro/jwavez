package com.rposcro.jwavez.serial.probe.transactions;

import com.rposcro.jwavez.serial.probe.exceptions.TransactionException;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class TransactionIdDispatcher {

  private AtomicLong higherIdSequence;
  private Queue<Byte> availableCallbackIds;
  private Map<Byte, TransactionId> activeTransactionIds;

  public TransactionIdDispatcher() {
    this.higherIdSequence = new AtomicLong(0x100);
    this.availableCallbackIds = new ConcurrentLinkedQueue<>();
    this.activeTransactionIds = new ConcurrentHashMap<>();
    IntStream.range(1, 254).forEach(val -> availableCallbackIds.add((byte) val));
  }

  TransactionId acquireId() {
    byte callbackId = Optional.ofNullable(availableCallbackIds.poll())
        .orElseThrow(() -> new TransactionException("No more available callback ids!"));
    TransactionId transactionId = nextTransactionId(callbackId);
    activeTransactionIds.put(callbackId, transactionId);
    return transactionId;
  }

  TransactionId activeForCallbackId(byte callbackId) {
    return activeTransactionIds.get(callbackId);
  }

  void releaseId(TransactionId transactionId) {
    availableCallbackIds.add(transactionId.getCallbackId());
    activeTransactionIds.remove(transactionId.getCallbackId());
  }

  private TransactionId nextTransactionId(byte callbackId) {
    long sequenceId = higherIdSequence.accumulateAndGet(1, (existing, increment) -> {
      if (++existing == 0) {
        existing = 0x100;
      }
      return existing;
    });
    return new TransactionId(sequenceId | ((int) callbackId & 0xff), callbackId);
  }
}
