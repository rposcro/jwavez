package com.rposcro.jwavez.serial.transactions;

import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class TransactionContext<T> {

  private final TransactionId transactionId;
  private final SerialTransaction transaction;
  private final CompletableFuture<TransactionResult<T>> futureResult;

  @Setter
  private boolean isActive;
}
