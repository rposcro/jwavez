package com.rposcro.jwavez.serial.controllers.helpers;

import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionKeeper<T extends TransactionState> {

  private T state;
  private boolean completed;
  private CompletableFuture<SerialRequest> transitRequestFuture;

  private Semaphore lock = new Semaphore(1);

  public Optional<SerialRequest> getTransitRequest() throws InterruptedException, ExecutionException {
    SerialRequest request = transitRequestFuture.get();
    transitRequestFuture = new CompletableFuture<>();
    return Optional.ofNullable(request);
  }

  public void transitAndSchedule(T state, SerialRequest transitRequest) {
    executeSynchronous(() -> this.doTransit(state, transitRequest));
  }

  public void transit(T state) {
    executeSynchronous(() -> this.doTransit(state, null));
  }

  public void complete() {
    executeSynchronous(() -> { this.completed = true; });
  }

  public void fail(Throwable t) {
    executeSynchronous(() -> {
      this.completed = true;
      this.transitRequestFuture.completeExceptionally(t);
    });
  }

  public T getState() {
    return getSynchronous(() -> this.state);
  }

  public boolean isCompleted() {
    return getSynchronous(() -> this.completed);
  }

  public boolean isTransitAllowed() {
    return getSynchronous(this::transitAllowed);
  }

  private void doTransit(T newState, SerialRequest nextRequest) {
    if (!transitAllowed()) {
      fail(new FlowException("Cannot transit when prior transition hasn't been consumed yet"));
    } else {
      this.state = newState;
      this.transitRequestFuture.complete(nextRequest);
    }
  }

  private boolean transitAllowed() {
    return this.transitRequestFuture.isDone();
  }

  private <T> T getSynchronous(Supplier<T> getter) {
    try {
      lock.acquireUninterruptibly();
      return getter.get();
    } finally {
      lock.release();
    }
  }

  private void executeSynchronous(Runnable task) {
    try {
      lock.acquireUninterruptibly();
      task.run();
    } finally {
      lock.release();
    }
  }
}
