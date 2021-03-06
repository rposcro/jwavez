package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.serial.controllers.helpers.TransactionState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AddNodeToNetworkFlowState implements TransactionState {

    WAITING_FOR_PROTOCOL(10_000),
    WAITING_FOR_NODE(60_000),
    NODE_FOUND(6_000),
    SLAVE_FOUND(76_000),
    CONTROLLER_FOUND(120_000),

    ABORTING_OPERATION(6_000),
    CLEANING_UP_ERRORS(6_000),
    TERMINATING_ADD_NODE(6_000),

    TERMINATION_STOP_SENT(-1),
    FAILURE_STOP_SENT(-1),
    CANCELLATION_STOP_SENT(-1);

    private long transitTimeout;
}
