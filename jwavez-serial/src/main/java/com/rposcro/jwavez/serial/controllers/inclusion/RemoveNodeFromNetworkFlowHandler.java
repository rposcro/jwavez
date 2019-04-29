package com.rposcro.jwavez.serial.controllers.inclusion;

import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.ABORTING_OPERATION;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.CANCELLATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.CLEANING_UP_ERRORS;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.CONTROLLER_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.FAILURE_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.NODE_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.SLAVE_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.TERMINATING_REMOVE_NODE;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.TERMINATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.WAITING_FOR_NODE;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.WAITING_FOR_PROTOCOL;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_DONE;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_FAILED;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_LEARN_READY;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_NODE_FOUND;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_REMOVING_CONTROLLER;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_REMOVING_SLAVE;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.frames.callbacks.RemoveNodeFromNetworkCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.requests.RemoveNodeFromNetworkRequest;
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class RemoveNodeFromNetworkFlowHandler extends AbstractFlowHandler {

  private TransactionKeeper<RemoveNodeFromNetworkFlowState> transactionKeeper;

  @Getter(AccessLevel.PACKAGE)
  private NodeInfo nodeInfo;
  private byte callbackFlowId;

  RemoveNodeFromNetworkFlowHandler(TransactionKeeper<RemoveNodeFromNetworkFlowState> transactionKeeper) {
    this.transactionKeeper = transactionKeeper;
  }

  @Override
  NodeId getNodeId() {
    return nodeInfo == null ? null : nodeInfo.getId();
  }

  @Override
  void handleCallback(ZWaveCallback zWaveCallback) {
    RemoveNodeFromNetworkCallback callback = verifyAndConvertCallback(zWaveCallback);
    RemoveNodeFromNetworkFlowState state = transactionKeeper.getState();
    RemoveNodeFromNeworkStatus status = callback.getStatus();
    Map<RemoveNodeFromNeworkStatus, Transition<RemoveNodeFromNetworkFlowHandler, RemoveNodeFromNetworkCallback, RemoveNodeFromNetworkFlowState>> transitionsMap = TRANSITIONS.get(state);
    Transition<RemoveNodeFromNetworkFlowHandler, RemoveNodeFromNetworkCallback, RemoveNodeFromNetworkFlowState> transition = transitionsMap == null ? null : transitionsMap.get(status);

    if (log.isDebugEnabled()) {
      log.debug("Callback received in state {}, flow id {}, status {}", state, callback.getCallbackFlowId(), callback.getStatus());
    }

    if (transition != null) {
      transition.getTransitionMethod().transit(this, callback, transition.getNewState());
    } else {
      interruptDueToStatus(status, state);
    }
  }

  @Override
  void startOver(byte callbackFlowId) {
    this.nodeInfo = null;
    this.callbackFlowId = callbackFlowId;
    this.transactionKeeper.transitAndSchedule(
        WAITING_FOR_PROTOCOL,
        RemoveNodeFromNetworkRequest.createStartRemoveAnyNodeRequest(callbackFlowId));
  }

  @Override
  void stopTransaction() {
    this.transactionKeeper.transitAndSchedule(
        RemoveNodeFromNetworkFlowState.CANCELLATION_STOP_SENT,
        finalStopFrame());
  }

  @Override
  void killTransaction() {
    this.transactionKeeper.transitAndSchedule(
        RemoveNodeFromNetworkFlowState.FAILURE_STOP_SENT,
        finalStopFrame());
  }

  void byPassTermination() {
    this.transactionKeeper.transitAndSchedule(
        TERMINATION_STOP_SENT,
        finalStopFrame());
  }

  private void transit(RemoveNodeFromNetworkFlowState newState) {
    transactionKeeper.transit(newState);
  }

  private void transit(RemoveNodeFromNetworkFlowState newState, SerialRequest request) {
    transactionKeeper.transitAndSchedule(newState, request);
  }

  private void readNodeAndTransit(RemoveNodeFromNetworkCallback callback, RemoveNodeFromNetworkFlowState newState) {
    nodeInfo = callback.getNodeInfo().orElse(nodeInfo);
    transactionKeeper.transit(newState);
  }

  private RemoveNodeFromNetworkCallback verifyAndConvertCallback(ZWaveCallback callback) {
    if (!(callback instanceof RemoveNodeFromNetworkCallback)) {
      interruptTransaction(new FlowException("Odd Callback class received while in node remove mode %s", callback.getClass()));
      return null;
    }
    RemoveNodeFromNetworkCallback removeCallback = (RemoveNodeFromNetworkCallback) callback;
    if (removeCallback.getCallbackFlowId() != callbackFlowId) {
      interruptTransaction(new FlowException("Incorrect callback flow id, expected %s while received %s", callbackFlowId, removeCallback.getCallbackFlowId()));
      return null;
    }
    return removeCallback;
  }

  private SerialRequest stoppingFrame() {
    return RemoveNodeFromNetworkRequest.createStopTransactionRequest(callbackFlowId);
  }

  private SerialRequest finalStopFrame() {
    return RemoveNodeFromNetworkRequest.createFinalTransactionRequest();
  }

  private void interruptTransaction(FlowException ex) {
    transactionKeeper.interrupt(ex);
  }

  private void interruptDueToStatus(RemoveNodeFromNeworkStatus status, RemoveNodeFromNetworkFlowState state) {
    transactionKeeper.interrupt(new FlowException("Received network status %s misses current transaction state %s", status, state));
  }

  private static final Map<RemoveNodeFromNetworkFlowState, Map<RemoveNodeFromNeworkStatus, Transition<RemoveNodeFromNetworkFlowHandler, RemoveNodeFromNetworkCallback, RemoveNodeFromNetworkFlowState>>> TRANSITIONS;

  static {
    TRANSITIONS = new HashMap<>();
    addTransition(WAITING_FOR_PROTOCOL, REMOVE_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, (h, c, s) -> h.transit(s, h.stoppingFrame()));
    addTransition(WAITING_FOR_PROTOCOL, REMOVE_NODE_STATUS_LEARN_READY, WAITING_FOR_NODE, (h, c, s) -> h.transit(s));

    addTransition(WAITING_FOR_NODE, REMOVE_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, (h, c, s) -> h.transit(s, h.stoppingFrame()));
    addTransition(WAITING_FOR_NODE, REMOVE_NODE_STATUS_NODE_FOUND, NODE_FOUND, (h, c, s) -> h.readNodeAndTransit(c, s));

    addTransition(NODE_FOUND, REMOVE_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, (h, c, s) -> h.transit(s, h.stoppingFrame()));
    addTransition(NODE_FOUND, REMOVE_NODE_STATUS_REMOVING_SLAVE, SLAVE_FOUND, (h, c, s) -> h.readNodeAndTransit(c, s));
    addTransition(NODE_FOUND, REMOVE_NODE_STATUS_REMOVING_CONTROLLER, CONTROLLER_FOUND, (h, c, s) -> h.readNodeAndTransit(c, s));

    addTransition(SLAVE_FOUND, REMOVE_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, (h, c, s) -> h.transit(s, h.stoppingFrame()));
    addTransition(SLAVE_FOUND, REMOVE_NODE_STATUS_DONE, TERMINATING_REMOVE_NODE, (h, c, s) -> h.transit(s, h.stoppingFrame()));

    addTransition(CONTROLLER_FOUND, REMOVE_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, (h, c, s) -> h.transit(s, h.stoppingFrame()));
    addTransition(CONTROLLER_FOUND, REMOVE_NODE_STATUS_DONE, TERMINATING_REMOVE_NODE, (h, c, s) -> h.transit(s, h.stoppingFrame()));

    addTransition(TERMINATING_REMOVE_NODE, REMOVE_NODE_STATUS_FAILED, FAILURE_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));
    addTransition(TERMINATING_REMOVE_NODE, REMOVE_NODE_STATUS_DONE, TERMINATION_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));

    addTransition(ABORTING_OPERATION, REMOVE_NODE_STATUS_FAILED, FAILURE_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));
    addTransition(ABORTING_OPERATION, REMOVE_NODE_STATUS_DONE, CANCELLATION_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));
    addTransition(ABORTING_OPERATION, REMOVE_NODE_STATUS_NODE_FOUND, NODE_FOUND, (h, c, s) -> h.transit(s));

    addTransition(CLEANING_UP_ERRORS, REMOVE_NODE_STATUS_FAILED, FAILURE_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));
    addTransition(CLEANING_UP_ERRORS, REMOVE_NODE_STATUS_DONE, FAILURE_STOP_SENT, (h, c, s) -> h.transit(s, h.finalStopFrame()));
  }

  private static void addTransition(
      RemoveNodeFromNetworkFlowState currentState,
      RemoveNodeFromNeworkStatus callbackStatus,
      RemoveNodeFromNetworkFlowState newState,
      TransitionMethod<RemoveNodeFromNetworkFlowHandler, RemoveNodeFromNetworkCallback, RemoveNodeFromNetworkFlowState> method) {
    TRANSITIONS.computeIfAbsent(currentState, (state) -> new HashMap<>())
        .put(callbackStatus, new Transition<>(newState, method));
  }
}
