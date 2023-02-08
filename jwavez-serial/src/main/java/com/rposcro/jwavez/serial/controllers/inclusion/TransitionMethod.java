package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.serial.controllers.helpers.TransactionState;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;

@FunctionalInterface
public interface TransitionMethod<H, C extends ZWaveCallback, T extends TransactionState> {

    void transit(H handler, C callback, T newState);
}
