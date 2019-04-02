package com.rposcro.jwavez.serial.controllers;

import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_ADDING_CONTROLLER;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_ADDING_SLAVE;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_DONE;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_FAILED;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_LEARN_READY;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_NODE_FOUND;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_PROTOCOL_DONE;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.controllers.helpers.CallbackFlowIdDispatcher;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionState;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.AddNodeToNetworkCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.requests.AddNodeToNetworkRequest;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkMode;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus;
import com.rposcro.jwavez.serial.rxtx.RxTxRouterProcess;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.async.AsynchronousExecutors;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddNodeToNetworkController {

  private static final long DEFAULT_TRANSACTION_TIMEOUT = 60_000;

  private ExecutorService executorService;
  private CallbackFlowIdDispatcher flowIdDispatcher;
  private RxTxRouterProcess rxTxRouterProcess;

  private Semaphore controllerLock;
  private TransactionKeeper<FlowState> transactionKeeper;

  private NodeInfo nodeInfo;
  private byte callbackFlowId;

  public Optional<NodeInfo> listenForNode() throws FlowException {
    try {
      controllerLock.acquireUninterruptibly();
      callbackFlowId = flowIdDispatcher.nextFlowId();
      executorService.invokeAny(
          Collections.singleton(() -> {
            this.flowLoop();
            return null;
          }),
          DEFAULT_TRANSACTION_TIMEOUT,
          TimeUnit.MILLISECONDS);
      return Optional.ofNullable(nodeInfo);
    } catch(TimeoutException e) {
      return Optional.empty();
    } catch(InterruptedException e) {
      throw new FlowException(e, "Transaction exception occurred!");
    } catch(ExecutionException e) {
      Throwable t = e.getCause();
      if (t instanceof FlowException) {
        throw (FlowException) t;
      } else {
        throw new FlowException(t, "Flow loop execution failed!");
      }
    } finally {
      transactionKeeper.transit(FlowState.IDLE);
      controllerLock.release();
    }
  }

  private void flowLoop() throws FlowException {
    transactionKeeper.transitAndSchedule(
        FlowState.WAITING_FOR_PROTOCOL,
        AddNodeToNetworkRequest.createAddNodeToNetworkRequest(AddNodeToNeworkMode.ADD_NODE_ANY, callbackFlowId, true, true));

    try {
      while (!transactionKeeper.isCompleted()) {
        Optional<SerialRequest> request = transactionKeeper.getTransitRequest();
        if (request.isPresent()) {
          rxTxRouterProcess.sendRequest(request.get());
        }
      }
    } catch(SerialException e) {
      throw new FlowException(e, "Transaction broken when sending request: " + e.getMessage());
    } catch(ExecutionException e) {
      throw new FlowException(e, "Transaction integrity violated: " + e.getMessage());
    } catch(CancellationException | InterruptedException e) {
      log.debug("Transaction interrupted", e);
    } catch(RuntimeException e) {
      throw new FlowException(e, "Transaction broken by unexpected cause: " + e.getMessage());
    }
  }

  private void callbackHandler(ZWaveCallback zWaveCallback) {
    FlowState state = transactionKeeper.getState();
    if (state == FlowState.IDLE) {
      return;
    }

    AddNodeToNetworkCallback callback = verifyAndConvertCallback(zWaveCallback);
    if (callback != null) {
      switch (state) {
        case WAITING_FOR_PROTOCOL:
          receivedAtWaitingForProtocol(callback);
          break;
        case WAITING_FOR_NODE:
          receivedAtWaitingForNode(callback);
          break;
        case NODE_FOUND:
          receivedAtNodeFound(callback);
          break;
        case SLAVE_FOUND:
          receivedAtSlaveFound(callback);
          break;
        case CONTROLLER_FOUND:
          receivedAtControllerFound(callback);
          break;
        case TERMINATING_ADD_NODE:
          receivedAtTerminatingAddNodeOperation(callback);
          break;
        case ABORTING_OPERATION:
          receivedAtAbortingOperation(callback);
          break;
        default:
          completeExceptionally(new FlowException("Unexpected flow state %s", state));
      }
    }
  }

  private void receivedAtWaitingForProtocol(AddNodeToNetworkCallback callback) {
    AddNodeToNeworkStatus status = callback.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      transactionKeeper.transitAndSchedule(FlowState.CLEANING_UP_ERRORS, stoppingFrame());
    } else if (status == ADD_NODE_STATUS_LEARN_READY) {
      transactionKeeper.transitAndSchedule(FlowState.WAITING_FOR_NODE, null);
    } else {
      completeExceptionally(new FlowException("Received network status %s misses current transaction state %s", status, transactionKeeper.getState()));
    }
  }

  private void receivedAtWaitingForNode(AddNodeToNetworkCallback callback) {
    AddNodeToNeworkStatus status = callback.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      transactionKeeper.transitAndSchedule(FlowState.CLEANING_UP_ERRORS, stoppingFrame());
    } else if (status == ADD_NODE_STATUS_NODE_FOUND) {
      nodeInfo = callback.getNodeInfo().orElse(null);
      transactionKeeper.transitAndSchedule(FlowState.NODE_FOUND, null);
    } else {
      completeExceptionally(new FlowException("Received network status %s misses current transaction state %s", status, transactionKeeper.getState()));
    }
  }

  private void receivedAtNodeFound(AddNodeToNetworkCallback callback) {
    AddNodeToNeworkStatus status = callback.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      transactionKeeper.transitAndSchedule(FlowState.CLEANING_UP_ERRORS, stoppingFrame());
    } else if (status == ADD_NODE_STATUS_ADDING_SLAVE) {
      nodeInfo = callback.getNodeInfo().orElse(nodeInfo);
      transactionKeeper.transitAndSchedule(FlowState.SLAVE_FOUND, null);
    } else if (status == ADD_NODE_STATUS_ADDING_CONTROLLER) {
      nodeInfo = callback.getNodeInfo().orElse(nodeInfo);
      transactionKeeper.transitAndSchedule(FlowState.CONTROLLER_FOUND, null);
    } else {
      completeExceptionally(new FlowException("Received network status %s misses current transaction state %s", status, transactionKeeper.getState()));
    }
  }

  private void receivedAtSlaveFound(AddNodeToNetworkCallback callback) {
    AddNodeToNeworkStatus status = callback.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      transactionKeeper.transitAndSchedule(FlowState.CLEANING_UP_ERRORS, stoppingFrame());
    } else if (status == ADD_NODE_STATUS_PROTOCOL_DONE) {
      transactionKeeper.transitAndSchedule(FlowState.TERMINATING_ADD_NODE, stoppingFrame());
    } else {
      completeExceptionally(new FlowException("Received network status %s misses current transaction state %s", status, transactionKeeper.getState()));
    }
  }

  private void receivedAtControllerFound(AddNodeToNetworkCallback callback) {
    AddNodeToNeworkStatus status = callback.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      transactionKeeper.transitAndSchedule(FlowState.CLEANING_UP_ERRORS, stoppingFrame());
    } else if (status == ADD_NODE_STATUS_PROTOCOL_DONE) {
      // TODO: Consider additional actions like SUC, SIS setup or controller replication
      transactionKeeper.transitAndSchedule(FlowState.TERMINATING_ADD_NODE, stoppingFrame());
    } else {
      completeExceptionally(new FlowException("Received network status %s misses current transaction state %s", status, transactionKeeper.getState()));
    }
  }

  private void receivedAtTerminatingAddNodeOperation(AddNodeToNetworkCallback callback) {
    AddNodeToNeworkStatus status = callback.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      transactionKeeper.transitAndSchedule(FlowState.TERMINATION_STOP_SENT, finalStopFrame());
    } else if (status == ADD_NODE_STATUS_DONE) {
      transactionKeeper.transitAndSchedule(FlowState.TERMINATION_STOP_SENT, finalStopFrame());
    } else {
      completeExceptionally(new FlowException("Received network status %s misses current transaction state %s", status, transactionKeeper.getState()));
    }
  }

  private void receivedAtAbortingOperation(AddNodeToNetworkCallback callback) {
    AddNodeToNeworkStatus status = callback.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      transactionKeeper.transitAndSchedule(FlowState.CANCELLATION_STOP_SENT, finalStopFrame());
    } else if (status == ADD_NODE_STATUS_DONE) {
      transactionKeeper.transitAndSchedule(FlowState.CANCELLATION_STOP_SENT, finalStopFrame());
    } else if (status == ADD_NODE_STATUS_NODE_FOUND) {
      transactionKeeper.transitAndSchedule(FlowState.NODE_FOUND, null);
    } else {
      completeExceptionally(new FlowException("Received network status %s misses current transaction state %s", status, transactionKeeper.getState()));
    }
  }

  private void receivedAtCleaningErrors(AddNodeToNetworkCallback callback) throws FlowException {
    AddNodeToNeworkStatus status = callback.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      transactionKeeper.transitAndSchedule(FlowState.CANCELLATION_STOP_SENT, finalStopFrame());
    } else if (status == ADD_NODE_STATUS_DONE) {
      transactionKeeper.transitAndSchedule(FlowState.CANCELLATION_STOP_SENT, finalStopFrame());
    } else {
      completeExceptionally(new FlowException("Received network status %s misses current transaction state %s", status, transactionKeeper.getState()));
    }
  }

  private AddNodeToNetworkCallback verifyAndConvertCallback(ZWaveCallback callback) {
    if (!(callback instanceof AddNodeToNetworkCallback)) {
      completeExceptionally(new FlowException("Odd Callback class received while in node add mode %s", callback.getClass()));
      return null;
    }
    AddNodeToNetworkCallback addCallback = (AddNodeToNetworkCallback) callback;
    if (addCallback.getCallbackFlowId() != callbackFlowId) {
      completeExceptionally(new FlowException("Incorrect callback flow id, expected %s while received %s", callbackFlowId, addCallback.getCallbackFlowId()));
      return null;
    }
    return addCallback;
  }

  private SerialRequest stoppingFrame() {
    return AddNodeToNetworkRequest.createStopTransactionRequest(callbackFlowId);
  }

  private SerialRequest finalStopFrame() {
    return AddNodeToNetworkRequest.createFinalTransactionRequest();
  }

  private void completeExceptionally(FlowException ex) {
    transactionKeeper.fail(ex);
  }

  @Builder
  private static AddNodeToNetworkController build(
      @NonNull CallbackFlowIdDispatcher flowIdDispatcher,
      @NonNull RxTxRouterProcess rxTxRouterProcess,
      ExecutorService executorService) {
    AddNodeToNetworkController instance = new AddNodeToNetworkController();
    instance.flowIdDispatcher = flowIdDispatcher;
    instance.rxTxRouterProcess = rxTxRouterProcess;
    instance.controllerLock = new Semaphore(1);
    instance.executorService = executorService == null ? AsynchronousExecutors.defaultExecutor() : executorService;
    return instance;
  }

  private enum FlowState implements TransactionState {
    IDLE,
    WAITING_FOR_PROTOCOL,
    WAITING_FOR_NODE,
    NODE_FOUND,
    ABORTING_OPERATION,
    SLAVE_FOUND,
    CONTROLLER_FOUND,
    CLEANING_UP_ERRORS,
    TERMINATING_ADD_NODE,

    TERMINATION_STOP_SENT,
    CANCELLATION_STOP_SENT
  }
}
