package com.rposcro.jwavez.serial;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.TransactionException;
import com.rposcro.jwavez.serial.frame.SOFFrameRegistry;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptor;
import com.rposcro.jwavez.serial.rxtx.InboundFrameProcessor;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;
import com.rposcro.jwavez.serial.transactions.SimpleRequestResponseTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionManager;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
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
  private SOFFrameRegistry frameRegistry;

  public <T> TransactionResult<T> sendFrameWithResponseAndWait(SOFRequestFrame requestFrame) {
    try {
      Future<TransactionResult<T>> future = sendFrameWithResponse(requestFrame);
      return future.get();
    } catch(CancellationException | ExecutionException | InterruptedException e) {
      throw new TransactionException("Failed to receive response", e);
    }
  }

  public <T> Future<TransactionResult<T>> sendFrameWithResponse(SOFRequestFrame requestFrame) {
    Class<? extends SOFResponseFrame> responseClass = frameRegistry.responseClass(requestFrame.getSerialCommand())
        .orElseThrow(() -> new SerialException("No response frame class found for serial command " + requestFrame.getSerialCommand()));
    SerialTransaction<T> transaction = new SimpleRequestResponseTransaction<>(requestFrame, responseClass);
    return executeTransaction(transaction);
  }

  public <T> Future<TransactionResult<T>> executeTransaction(SerialTransaction<T> transaction) {
    return transactionManager.scheduleTransaction(transaction);
  }

  public void addInboundFrameInterceptor(InboundFrameInterceptor interceptor) {
    this.inboundFrameProcessor.addInterceptor(interceptor);
  }
}
