package com.rposcro.jwavez.serial;

import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptor;
import com.rposcro.jwavez.serial.rxtx.InboundFrameProcessor;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionManager;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import java.util.concurrent.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@AllArgsConstructor
public class SerialChannel {

  private TransactionManager transactionManager;
  private InboundFrameProcessor inboundFrameProcessor;

  public <T> Future<TransactionResult<T>> executeTransaction(SerialTransaction<T> transaction) {
    return transactionManager.scheduleTransaction(transaction);
  }

  public void addInboundFrameInterceptor(InboundFrameInterceptor interceptor) {
    this.inboundFrameProcessor.addInterceptor(interceptor);
  }
}
