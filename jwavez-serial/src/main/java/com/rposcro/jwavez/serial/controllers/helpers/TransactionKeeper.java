package com.rposcro.jwavez.serial.controllers.helpers;

import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionKeeper<T extends TransactionState> {

  private T state;
  private boolean successful;
  private boolean failed;
  private boolean cancelled;

  private SerialRequest nextRequest;
  private Throwable nextException;
  private Semaphore lock = new Semaphore(1);

  private Consumer<T> stateChangeListener;

  public TransactionKeeper(@NonNull Consumer<T> stateChangeListener) {
    this.stateChangeListener = stateChangeListener;
  }

  public void reset() {
    this.nextRequest = null;
    this.successful = false;
    this.failed = false;
  }

  public void transitAndSchedule(T state, SerialRequest transitRequest) {
    executeSynchronous(() -> this.doTransit(state, transitRequest));
  }

  public void transit(T state) {
    executeSynchronous(() -> this.doTransit(state, null));
  }

  public void interrupt(Throwable t) {
    nextException = t;
  }


  public Optional<SerialRequest> getTransitRequest() {
    return getSynchronous(() -> {
      if (transitInProgress()) {
        try {
          if (nextException != null) {
            throw new CompletionException(nextException);
          } else {
            return Optional.of(nextRequest);
          }
        } finally {
          nextException = null;
          nextRequest = null;
        }
      } else {
        return Optional.empty();
      }
    });
  }

  public void complete() {
    executeSynchronous(() -> { this.successful = true; });
  }

  public void cancel() {
    executeSynchronous(() -> { this.cancelled = true; });
  }

  public void fail() {
    executeSynchronous(() -> { this.failed = true; });
  }


  public T getState() {
    return getSynchronous(() -> this.state);
  }

  public boolean isSuccessful() {
    return getSynchronous(() -> this.successful);
  }

  public boolean isCancelled() {
    return getSynchronous(() -> this.cancelled);
  }

  public boolean isFailed() {
    return getSynchronous(() -> this.failed);
  }

  public boolean isStopped() {
    return getSynchronous(() -> this.failed || this.successful || this.cancelled);
  }


  private void doTransit(T newState, SerialRequest nextRequest) {
    if (!transitAllowed()) {
      nextException = new FlowException("Cannot transit when prior transition hasn't been consumed yet");
    } else {
      log.info("Transiting from {} to {} state, {}", this.state, newState, nextRequest != null ? "flow id " + nextRequest.getCallbackFlowId() : "no request");
      this.state = newState;
      this.nextRequest = nextRequest;
      this.stateChangeListener.accept(state);
    }
  }

  private boolean transitAllowed() {
    return !(successful || failed || transitInProgress());
  }

  private boolean transitInProgress() {
    return nextRequest != null || nextException != null;
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
