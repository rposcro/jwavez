package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.serial.controllers.helpers.TransactionState;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Transition<H, C extends ZWaveCallback, T extends TransactionState> {

  private T newState;
  private TransitionMethod<H, C, T> transitionMethod;
}
