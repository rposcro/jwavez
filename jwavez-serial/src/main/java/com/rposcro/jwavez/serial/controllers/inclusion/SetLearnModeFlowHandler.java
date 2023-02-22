package com.rposcro.jwavez.serial.controllers.inclusion;

import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_ACTIVATED;
import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_CANCELLED;
import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_DONE;
import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_FAILED;
import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_STARTED;
import static com.rposcro.jwavez.serial.model.LearnStatus.LEARN_STATUS_DONE;
import static com.rposcro.jwavez.serial.model.LearnStatus.LEARN_STATUS_FAILED;
import static com.rposcro.jwavez.serial.model.LearnStatus.LEARN_STATUS_SECURITY_FAILED;
import static com.rposcro.jwavez.serial.model.LearnStatus.LEARN_STATUS_STARTED;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationUpdateCallback;
import com.rposcro.jwavez.serial.frames.callbacks.SetLearnModeCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.requests.SetLearnModeRequestBuilder;
import com.rposcro.jwavez.serial.model.LearnMode;
import com.rposcro.jwavez.serial.model.LearnStatus;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class SetLearnModeFlowHandler extends AbstractFlowHandler {

    private final TransactionKeeper<SetLearnModeFlowState> transactionKeeper;
    private final SetLearnModeRequestBuilder setLearnModeRequestBuilder;

    @Getter(AccessLevel.PACKAGE)
    private byte callbackFlowId;

    @Getter(AccessLevel.PACKAGE)
    private NodeId nodeId;

    SetLearnModeFlowHandler(TransactionKeeper<SetLearnModeFlowState> transactionKeeper,
                            SetLearnModeRequestBuilder setLearnModeRequestBuilder) {
        this.transactionKeeper = transactionKeeper;
        this.setLearnModeRequestBuilder = setLearnModeRequestBuilder;
    }

    @Override
    void handleCallback(ZWaveCallback zWaveCallback) {
        SetLearnModeCallback callback = verifyAndConvertCallback(zWaveCallback);

        if (callback == null) {
            return;
        }

        SetLearnModeFlowState state = transactionKeeper.getState();
        LearnStatus status = callback.getLearnStatus();
        Map<LearnStatus, Transition<SetLearnModeFlowHandler, SetLearnModeCallback, SetLearnModeFlowState>> transitionsMap = TRANSITIONS.get(state);
        Transition<SetLearnModeFlowHandler, SetLearnModeCallback, SetLearnModeFlowState> transition = transitionsMap == null ? null : transitionsMap.get(status);

        if (log.isDebugEnabled()) {
            log.debug("Callback received in state {}, flow id {}, status {}", state, callback.getCallbackFlowId(), callback.getLearnStatus());
        }

        if (transition != null) {
            transition.getTransitionMethod().transit(this, callback, transition.getNewState());
        } else {
            interruptDueToStatus(status, state);
        }
    }

    @Override
    void startOver(byte callbackFlowId) {
        this.nodeId = null;
        this.callbackFlowId = callbackFlowId;
        this.transactionKeeper.transitAndSchedule(
                LEARN_MODE_ACTIVATED,
                setLearnModeRequestBuilder.createSetLearnModeRequest(LearnMode.LEARN_MODE_CLASSIC, callbackFlowId));
    }

    @Override
    void stopTransaction() {
        this.transactionKeeper.transitAndSchedule(LEARN_MODE_CANCELLED, disableLearnModeFrame());
    }

    @Override
    void killTransaction() {
        this.transactionKeeper.transitAndSchedule(LEARN_MODE_FAILED, disableLearnModeFrame());
    }

    private void readNodeIdAndTransit(SetLearnModeFlowState newState, SetLearnModeCallback callback) {
        this.nodeId = callback.getNodeId();
        transactionKeeper.transit(newState);
    }

    private void transit(SetLearnModeFlowState newState) {
        transactionKeeper.transit(newState);
    }

    private SetLearnModeCallback verifyAndConvertCallback(ZWaveCallback callback) {
        if (callback instanceof ApplicationUpdateCallback) {
            ApplicationUpdateCallback updateCallback = (ApplicationUpdateCallback) callback;
            log.info("Application Update Callback received: {}", updateCallback.getStatus());
            return null;
        }

        if (!(callback instanceof SetLearnModeCallback)) {
            interruptTransaction(new FlowException("Odd Callback class received while in node learn mode %s", callback.getClass()));
            return null;
        }

        SetLearnModeCallback learnCallback = (SetLearnModeCallback) callback;
        if (learnCallback.getCallbackFlowId() != callbackFlowId) {
            interruptTransaction(new FlowException("Incorrect callback flow id, expected %s while received %s", callbackFlowId, learnCallback.getCallbackFlowId()));
            return null;
        }
        return learnCallback;
    }

    private SerialRequest disableLearnModeFrame() {
        return setLearnModeRequestBuilder.createSetLearnModeRequest(LearnMode.LEARN_MODE_DISABLE, callbackFlowId);
    }

    private void interruptTransaction(FlowException ex) {
        transactionKeeper.interrupt(ex);
    }

    private void interruptDueToStatus(LearnStatus status, SetLearnModeFlowState state) {
        transactionKeeper.interrupt(new FlowException("Received network status %s misses current transaction state %s", status, state));
    }

    private static final Map<SetLearnModeFlowState, Map<LearnStatus, Transition<SetLearnModeFlowHandler, SetLearnModeCallback, SetLearnModeFlowState>>> TRANSITIONS;

    static {
        TRANSITIONS = new HashMap<>();
        addTransition(LEARN_MODE_ACTIVATED, LEARN_STATUS_STARTED, LEARN_MODE_STARTED, (h, c, s) -> h.transit(s));

        addTransition(LEARN_MODE_STARTED, LEARN_STATUS_STARTED, LEARN_MODE_STARTED, (h, c, s) -> h.transit(s));
        addTransition(LEARN_MODE_STARTED, LEARN_STATUS_DONE, LEARN_MODE_DONE, (h, c, s) -> h.readNodeIdAndTransit(s, c));
        addTransition(LEARN_MODE_STARTED, LEARN_STATUS_FAILED, LEARN_MODE_FAILED, (h, c, s) -> h.transit(s));
        addTransition(LEARN_MODE_STARTED, LEARN_STATUS_SECURITY_FAILED, LEARN_MODE_FAILED, (h, c, s) -> h.transit(s));
    }

    private static void addTransition(
            SetLearnModeFlowState currentState,
            LearnStatus callbackStatus,
            SetLearnModeFlowState newState,
            TransitionMethod<SetLearnModeFlowHandler, SetLearnModeCallback, SetLearnModeFlowState> method) {
        TRANSITIONS.computeIfAbsent(currentState, (state) -> new HashMap<>())
                .put(callbackStatus, new Transition<>(newState, method));
    }
}
