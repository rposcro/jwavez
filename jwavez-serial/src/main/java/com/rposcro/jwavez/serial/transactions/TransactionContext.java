package com.rposcro.jwavez.serial.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class TransactionContext {

  private final TransactionId transactionId;
  private final SerialTransaction transaction;
  private final long timeout;

  @Setter
  private boolean isActive;

  public byte getCallbackId() {
    return transactionId.getCallbackId();
  }
}
