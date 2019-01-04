package com.rposcro.jwavez.serial.transactions;

import static com.rposcro.jwavez.serial.frame.SOFFrame.OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.exceptions.TransactionException;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.frame.constants.FrameType;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * @param <T> Transaction result type
 */
@Slf4j
abstract class AbstractSerialTransaction<T> implements SerialTransaction<T> {

  protected TransactionContext<T> transactionContext;
  protected TransactionStatus status;

  private boolean expectsCallbacks;
  private boolean expectsResponse;

  protected AbstractSerialTransaction(boolean expectsCallbacks, boolean expectsResponse) {
    this.status = TransactionStatus.Active;
    this.expectsCallbacks = expectsCallbacks;
    this.expectsResponse = expectsResponse;
  }

  @Override
  public TransactionContext<T> init(TransactionId transactionId) {
    this.transactionContext = (TransactionContext<T>) TransactionContext.builder()
        .transaction(this)
        .transactionId(transactionId)
        .futureResult(new CompletableFuture<>())
        .isActive(true)
        .build();
    return transactionContext;
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
    transactionContext.getFutureResult().complete(new TransactionResult<>(status, result));
  }

  protected void failTransaction() {
    this.status = TransactionStatus.Failed;
    log.debug("Transaction stopped with status {}", status);
    transactionContext.setActive(false);
    transactionContext.getFutureResult().complete(new TransactionResult<>(status, null));
  }

  protected void cancelTransaction() {
    this.status = TransactionStatus.Cancelled;
    log.debug("Transaction stopped with status {}", status);
    transactionContext.setActive(false);
    transactionContext.getFutureResult().complete(new TransactionResult<>(status, null));
  }

  protected <T extends SOFCallbackFrame> T validateCallbackAndCast(SOFFrame frame, byte callbackId) throws TransactionException {
    if (frame.getFrameType() == FrameType.REQ
        && frame.getSerialCommand() == SerialCommand.SET_SUC_NODE_ID
        && frame.getBuffer()[OFFSET_PAYLOAD] == callbackId) {
      try {
        return (T) frame;
      } catch(ClassCastException e) {
        throw new TransactionException("Callback frame validation failed, stopping");
      }
    }
    throw new TransactionException("Callback frame validation failed, stopping");
  }

  protected <T extends SOFResponseFrame> T validateResponseAndCast(SOFFrame frame) throws TransactionException {
    if (frame.getFrameType() == FrameType.RES
        && frame.getSerialCommand() == SerialCommand.SET_SUC_NODE_ID) {
      try {
        return (T) frame;
      } catch(ClassCastException e) {
        throw new TransactionException("Response frame validation failed, stopping");
      }
    }
    throw new TransactionException("Response frame validation failed, stopping");
  }
}
