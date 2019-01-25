package com.rposcro.jwavez.serial.probe.factory;

import com.rposcro.jwavez.serial.probe.transactions.TransactionIdDispatcher;
import com.rposcro.jwavez.serial.probe.transactions.TransactionManager;
import com.rposcro.jwavez.serial.probe.rxtx.SerialCommunicationBroker;
import java.util.concurrent.Semaphore;

public class TransactionServicesFactory {

  private static final Semaphore semaphore = new Semaphore(1);
  private static TransactionServicesFactory singleton;

  private TransactionIdDispatcher transactionIdDispatcher;

  private TransactionServicesFactory() {
    transactionIdDispatcher = new TransactionIdDispatcher();
  }

  public TransactionManager createTransactionManager(SerialCommunicationBroker communicationBroker) {
    return TransactionManager.builder()
        .callbackIdDispatcher(transactionIdDispatcher)
        .communicationBroker(communicationBroker)
        .build();
  }

  public static TransactionServicesFactory custom() {
    semaphore.acquireUninterruptibly();
    try {
      if (singleton == null) {
        singleton = new TransactionServicesFactory();
      }
      return singleton;
    } finally {
      semaphore.release();
    }
  }
}
