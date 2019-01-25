package com.rposcro.jwavez.serial.probe.transactions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionId {

  private long id;
  private byte callbackId;
}
