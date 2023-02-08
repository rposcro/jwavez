package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.serial.controllers.helpers.TransactionState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SetLearnModeFlowState implements TransactionState {

    LEARN_MODE_ACTIVATED(15_000),
    LEARN_MODE_STARTED(30_000),
    LEARN_MODE_DONE(-1),
    LEARN_MODE_CANCELLED(-1),
    LEARN_MODE_FAILED(-1);

    private long transitTimeout;
}
