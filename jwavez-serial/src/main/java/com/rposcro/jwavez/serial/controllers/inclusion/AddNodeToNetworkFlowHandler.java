package com.rposcro.jwavez.serial.controllers.inclusion;

import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.ABORTING_OPERATION;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.CANCELLATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.CLEANING_UP_ERRORS;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.CONTROLLER_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.FAILURE_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.NODE_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.SLAVE_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.TERMINATING_ADD_NODE;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.TERMINATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.WAITING_FOR_NODE;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.WAITING_FOR_PROTOCOL;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_ADDING_CONTROLLER;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_ADDING_SLAVE;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_DONE;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_FAILED;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_LEARN_READY;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_NODE_FOUND;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_PROTOCOL_DONE;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.frames.callbacks.AddNodeToNetworkCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.requests.AddNodeToNetworkRequest;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkMode;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class AddNodeToNetworkFlowHandler extends AbstractFlowHandler {

  private TransactionKeeper<AddNodeToNetworkFlowState> transactionKeeper;

  @Getter(AccessLevel.PACKAGE)
  private NodeInfo nodeInfo;
  private byte callbackFlowId;

  AddNodeToNetworkFlowHandler(TransactionKeeper<AddNodeToNetworkFlowState> transactionKeeper) {
    this.transactionKeeper = transactionKeeper;
  }

  @Override
  void startOver(byte callbackFlowId) {
    this.nodeInfo = null;
    this.callbackFlowId = callbackFlowId;
    this.transactionKeeper.transitAndSchedule(
        AddNodeToNetworkFlowState.WAITING_FOR_PROTOCOL,
        AddNodeToNetworkRequest.createAddNodeToNetworkRequest(AddNodeToNeworkMode.ADD_NODE_ANY, callbackFlowId, true, true));
  }

  @Override
  void stopTransaction() {
    this.transactionKeeper.transitAndSchedule(
        AddNodeToNetworkFlowState.CANCELLATION_STOP_SENT,
        finalStopFrame());
  }

  @Override
  void killTransaction() {
    this.transactionKeeper.transitAndSchedule(
        AddNodeToNetworkFlowState.FAILURE_STOP_SENT,
        finalStopFrame());
  }

  @Override
  void handleCallback(ZWaveCallback zWaveCallback) {
    AddNodeToNetworkCallback callback = verifyAndConvertCallback(zWaveCallback);
    AddNodeToNetworkFlowState state = transactionKeeper.getState();
    AddNodeToNeworkStatus status = callback.getStatus();
    Map<AddNodeToNeworkStatus, Transition<AddNodeToNetworkFlowHandler, AddNodeToNetworkCallback, AddNodeToNetworkFlowState>> transitionsMap = TRANSITIONS.get(state);
    Transition<AddNodeToNetworkFlowHandler, AddNodeToNetworkCallback, AddNodeToNetworkFlowState> transition = transitionsMap == null ? null : transitionsMap.get(status);

    if (log.isDebugEnabled()) {
      log.debug("Callback received in state {}, flow id {}, status {}", state, callback.getCallbackFlowId(), callback.getStatus());
    }

    if (transition != null) {
      transition.getTransitionMethod().transit(this, callback, transition.getNewState());
    } else {
      interruptDueToStatus(status, state);
    }
  }

  private AddNodeToNetworkCallback verifyAndConvertCallback(ZWaveCallback callback) {
    if (!(callback instanceof AddNodeToNetworkCallback)) {
      interruptTransaction(new FlowException("Odd Callback class received while in node add mode %s", callback.getClass()));
      return null;
    }
    AddNodeToNetworkCallback addCallback = (AddNodeToNetworkCallback) callback;
    if (addCallback.getCallbackFlowId() != callbackFlowId) {
      interruptTransaction(new FlowException("Incorrect callback flow id, expected %s while received %s", callbackFlowId, addCallback.getCallbackFlowId()));
      return null;
    }
    return addCallback;
  }

  private void transit(AddNodeToNetworkFlowState newState) {
    transactionKeeper.transit(newState);
  }

  private void transit(AddNodeToNetworkFlowState newState, SerialRequest request) {
    transactionKeeper.transitAndSchedule(newState, request);
  }

  private void readNodeAndTransit(AddNodeToNetworkCallback callback, AddNodeToNetworkFlowState newState) {
    nodeInfo = callback.getNodeInfo().orElse(nodeInfo);
    transactionKeeper.transit(newState);
  }

  private SerialRequest stoppingFrame() {
    return AddNodeToNetworkRequest.createStopTransactionRequest(callbackFlowId);
  }

  private SerialRequest finalStopFrame() {
    return AddNodeToNetworkRequest.createFinalTransactionRequest();
  }

  private void interruptTransaction(FlowException ex) {
    transactionKeeper.interrupt(ex);
  }

  private void interruptDueToStatus(AddNodeToNeworkStatus status, AddNodeToNetworkFlowState state) {
    transactionKeeper.interrupt(new FlowException("Received network status %s misses current transaction state %s", status, state));
  }

  private static final Map<AddNodeToNetworkFlowState, Map<AddNodeToNeworkStatus, Transition<AddNodeToNetworkFlowHandler, AddNodeToNetworkCallback, AddNodeToNetworkFlowState>>> TRANSITIONS;

  static {
    TRANSITIONS = new HashMap<>();
    addTransition(WAITING_FOR_PROTOCOL, ADD_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, (h, c, s) -> h.transit(s, h.stoppingFrame()));
    addTransition(WAITING_FOR_PROTOCOL, ADD_NODE_STATUS_LEARN_READY, WAITING_FOR_NODE, (h, c, s) -> h.transit(s));

    addTransition(WAITING_FOR_NODE, ADD_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, (h, c, s) -> h.transit(s, h.stoppingFrame()));
    addTransition(WAITING_FOR_NODE, ADD_NODE_STATUS_NODE_FOUND, NODE_FOUND, (h, c, s) -> h.readNodeAndTransit(c, s));

    addTransition(NODE_FOUND, ADD_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, (h, c, s) -> h.transit(s, h.stoppingFrame()));
    addTransition(NODE_FOUND, ADD_NODE_STATUS_ADDING_SLAVE, SLAVE_FOUND, (h, c, s) -> h.readNodeAndTransit(c, s));
    addTransition(NODE_FOUND, ADD_NODE_STATUS_ADDING_CONTROLLER, CONTROLLER_FOUND, (h, c, s) -> h.readNodeAndTransit(c, s));

    addTransition(SLAVE_FOUND, ADD_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, (h, c, s) -> h.transit(s, h.stoppingFrame()));
    addTransition(SLAVE_FOUND, ADD_NODE_STATUS_PROTOCOL_DONE, TERMINATING_ADD_NODE, (h, c, s) -> h.transit(s, h.stoppingFrame()));

    addTransition(CONTROLLER_FOUND, ADD_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, (h, c, s) -> h.transit(s, h.stoppingFrame()));
    addTransition(CONTROLLER_FOUND, ADD_NODE_STATUS_PROTOCOL_DONE, TERMINATING_ADD_NODE, (h, c, s) -> h.transit(s, h.stoppingFrame()));

    addTransition(TERMINATING_ADD_NODE, ADD_NODE_STATUS_FAILED, FAILURE_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));
    addTransition(TERMINATING_ADD_NODE, ADD_NODE_STATUS_DONE, TERMINATION_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));

    addTransition(ABORTING_OPERATION, ADD_NODE_STATUS_FAILED, FAILURE_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));
    addTransition(ABORTING_OPERATION, ADD_NODE_STATUS_DONE, CANCELLATION_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));
    addTransition(ABORTING_OPERATION, ADD_NODE_STATUS_NODE_FOUND, NODE_FOUND, (h, c, s) -> h.transit(s));

    addTransition(CLEANING_UP_ERRORS, ADD_NODE_STATUS_FAILED, FAILURE_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));
    addTransition(CLEANING_UP_ERRORS, ADD_NODE_STATUS_DONE, FAILURE_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));
  }

  private static void addTransition(
      AddNodeToNetworkFlowState currentState,
      AddNodeToNeworkStatus callbackStatus,
      AddNodeToNetworkFlowState newState,
      TransitionMethod<AddNodeToNetworkFlowHandler, AddNodeToNetworkCallback, AddNodeToNetworkFlowState> method) {
    TRANSITIONS.computeIfAbsent(currentState, (state) -> new HashMap<>())
        .put(callbackStatus, new Transition<>(newState, method));
  }
}
