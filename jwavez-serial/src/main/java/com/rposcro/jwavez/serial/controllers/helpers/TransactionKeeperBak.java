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
public class TransactionKeeperBak<T extends TransactionState> {

  private T state;
  private CompletableFuture<SerialRequest> transitRequestFuture;
  private boolean completed;

  private Semaphore lock = new Semaphore(1);

  public Optional<SerialRequest> getTransitRequest() throws InterruptedException, ExecutionException {
    SerialRequest request = transitRequestFuture.get();
    transitRequestFuture = new CompletableFuture<>();
    return Optional.ofNullable(request);
  }

  public void transitAndSchedule(T state, SerialRequest transitRequest) throws FlowException {
    executeSynchronous(() -> {
      if (!transitAllowed()) {
        throw new FlowException("Cannot transit when prior transition hasn't been consumed yet");
      }
      this.state = state;
      this.transitRequestFuture.complete(transitRequest);
    });
  }

  public void transit(T state) throws FlowException {
    executeSynchronous(() -> {
      if (!transitAllowed()) {
        throw new FlowException("Cannot transit when prior transition hasn't been consumed yet");
      }
      this.state = state;
      this.transitRequestFuture.complete(null);
    });
  }

  public void complete() throws FlowException {
    executeSynchronous(() -> { this.completed = true; });
  }

  public void fail(Throwable t) throws FlowException {
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

  private void executeSynchronous(Executor task) throws FlowException {
    try {
      lock.acquireUninterruptibly();
      task.doIt();
    } finally {
      lock.release();
    }
  }

  @FunctionalInterface
  private interface Executor {
    void doIt() throws FlowException;
  }
}
