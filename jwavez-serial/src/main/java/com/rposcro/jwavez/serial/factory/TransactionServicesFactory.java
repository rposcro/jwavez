package com.rposcro.jwavez.serial.factory;

import com.rposcro.jwavez.serial.rxtx.InboundFrameProcessor;
import com.rposcro.jwavez.serial.rxtx.SerialCommunicationBroker;
import com.rposcro.jwavez.serial.transactions.TransactionIdDispatcher;
import com.rposcro.jwavez.serial.transactions.TransactionManager;
import java.util.concurrent.Semaphore;

class TransactionServicesFactory {

  private static final Semaphore semaphore = new Semaphore(1);
  private static TransactionServicesFactory singleton;

  private TransactionIdDispatcher transactionIdDispatcher;

  TransactionManager createTransactionManager(InboundFrameProcessor frameProcessor, SerialCommunicationBroker communicationBroker) {
    return TransactionManager.builder()
        .callbackIdDispatcher(transactionIdDispatcher)
        .frameProcessor(frameProcessor)
        .communicationBroker(communicationBroker)
        .build();
  }

  static TransactionServicesFactory custom() {
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
