package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.controllers.helpers.CallbackFlowIdDispatcher;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionState;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.rxtx.RxTxRouterProcess;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractInclusionController<T extends TransactionState> {

  protected Semaphore controllerLock;
  protected RxTxRouterProcess rxTxRouterProcess;
  protected TransactionKeeper<T> transactionKeeper;
  protected AbstractFlowHandler flowHandler;
  protected CallbackFlowIdDispatcher flowIdDispatcher;

  protected long waitForTouchTimeout;
  protected long waitForProgressTimeout;

  protected long transitTimeoutPoint;

  protected AbstractInclusionController() {
    this.controllerLock = new Semaphore(1);
  }

  protected abstract boolean isWaitingForTouchState(T state);
  protected abstract boolean isFinalState(T state);
  protected abstract void finalizeTransaction(T state);
  protected abstract void timeoutTransaction(T state);

  protected Optional<NodeInfo> runTransaction(String processName) throws FlowException {
    long transactionId = System.currentTimeMillis();
    log.info("Requested {} node from network with transaction id {}", processName, transactionId);
    controllerLock.acquireUninterruptibly();
    log.info("Transaction launched {}", transactionId);
    try {
      transactionKeeper.reset();
      flowHandler.startOver(flowIdDispatcher.nextFlowId());

      do {
        flowStep();
      } while (!transactionKeeper.isStopped());

      if (transactionKeeper.isSuccessful()) {
        NodeInfo nodeInfo = flowHandler.getNodeInfo();
        log.info("Transaction successful {}, {} node id {}", transactionId, processName, nodeInfo == null ? "UNKNOWN" : nodeInfo.getId().getId());
        return Optional.ofNullable(nodeInfo);
      } else if (transactionKeeper.isCancelled()) {
        log.info("Transaction cancelled due to no node to {} appeared {}", processName, transactionId);
        return Optional.empty();
      }

      log.info("Transaction failed!! {}", transactionId);
      throw new FlowException("Process of %s node from network transaction failed at state %s", processName, transactionKeeper.getState());
    } finally {
      log.info("Process of {} node transaction finished, transaction id {}", processName, transactionId);
      controllerLock.release();
    }
  }

  protected void flowStep() throws FlowException {
    try {
      Optional<SerialRequest> requestWrapper = transactionKeeper.getTransitRequest();
      if (requestWrapper.isPresent()) {
        SerialRequest request = requestWrapper.get();
        log.debug("Sending request: {} {}", request.getSerialCommand(), request.getCallbackFlowId());
        rxTxRouterProcess.sendRequest(request);
      }

      T state = transactionKeeper.getState();
      if (isFinalState(state)) {
        finalizeTransaction(state);
      } else if (System.currentTimeMillis() > transitTimeoutPoint) {
        timeoutTransaction(state);
      }
    } catch(SerialException e) {
      transactionKeeper.fail();
      throw new FlowException(e, "Transaction broken when sending request: " + e.getMessage());
    } catch(CompletionException e) {
      transactionKeeper.fail();
      throw new FlowException(e, "Transaction integrity violated: " + e.getMessage());
    } catch(CancellationException e) {
      transactionKeeper.fail();
      log.debug("Transaction interrupted", e);
    } catch(RuntimeException e) {
      transactionKeeper.fail();
      throw new FlowException(e, "Transaction broken by unexpected cause: " + e.getMessage());
    }
  }

  protected void transactionStateChanged(T newState) {
    if (isWaitingForTouchState(newState) && waitForTouchTimeout > 0) {
      transitTimeoutPoint = System.currentTimeMillis() + waitForTouchTimeout;
    } else if (!isFinalState(newState) && waitForProgressTimeout > 0) {
      transitTimeoutPoint = System.currentTimeMillis() + waitForProgressTimeout;
    } else {
      transitTimeoutPoint = System.currentTimeMillis() + newState.getTransitTimeout();
    }
  }
}
