package com.rposcro.jwavez.serial.configuration;

import com.rposcro.jwavez.serial.transactions.TransactionIdDispatcher;
import com.rposcro.jwavez.serial.transactions.TransactionManager;
import lombok.Getter;

@Getter
public class TransactionConfiguration {

  private TransactionIdDispatcher transactionIdDispatcher;
  private TransactionManager transactionManager;

  public TransactionConfiguration(RxTxConfiguration rxTxConfiguration) {
    this.transactionIdDispatcher = new TransactionIdDispatcher();
    this.transactionManager = TransactionManager.builder()
        .callbackIdDispatcher(transactionIdDispatcher)
        .frameProcessor(rxTxConfiguration.getInboundFrameProcessor())
        .communicationBroker(rxTxConfiguration.getSerialCommunicationBroker())
        .build();
  }
}
