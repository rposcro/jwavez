package com.rposcro.jwavez.serial.transactions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class TransactionResult<T> {

  private TransactionStatus status;
  private T result;
}
