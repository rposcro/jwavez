package com.rposcro.jwavez.serial.transactions;

import static com.rposcro.jwavez.serial.frame.SOFFrame.OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.exceptions.TransactionException;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.frame.constants.FrameType;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

/**
 * @param <T> Transaction result type
 */
@Slf4j
abstract class AbstractSerialTransaction<T> implements SerialTransaction<T> {

  protected TransactionContext transactionContext;
  protected CompletableFuture<TransactionResult<T>> futureResult;
  protected TransactionStatus status;

  private boolean expectsCallbacks;
  private boolean expectsResponse;

  protected AbstractSerialTransaction(boolean expectsCallbacks, boolean expectsResponse) {
    this.status = TransactionStatus.Active;
    this.expectsCallbacks = expectsCallbacks;
    this.expectsResponse = expectsResponse;
  }

  @Override
  public Future<TransactionResult<T>> init(TransactionContext transactionContext) {
    this.transactionContext = transactionContext;
    this.futureResult = new CompletableFuture<>();
    return this.futureResult;
  }

  @Override
  public boolean expectsCallbacks() {
    return expectsCallbacks;
  }

  @Override
  public boolean expectsResponse() {
    return expectsResponse;
  }

  @Override
  public TransactionStatus status() {
    return this.status;
  }

  protected void completeTransaction(T result) {
    this.status = TransactionStatus.Completed;
    log.debug("Transaction stopped with status {}", status);
    transactionContext.setActive(false);
    futureResult.complete(new TransactionResult<>(status, result));
  }

  protected void failTransaction() {
    this.status = TransactionStatus.Failed;
    log.debug("Transaction stopped with status {}", status);
    transactionContext.setActive(false);
    futureResult.complete(new TransactionResult<>(status, null));
  }

  protected void cancelTransaction() {
    this.status = TransactionStatus.Cancelled;
    log.debug("Transaction stopped with status {}", status);
    transactionContext.setActive(false);
    futureResult.complete(new TransactionResult<>(status, null));
  }
}
