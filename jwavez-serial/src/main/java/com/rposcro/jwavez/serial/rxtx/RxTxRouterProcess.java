package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.exceptions.FatalSerialException;
import com.rposcro.jwavez.serial.exceptions.RequestFlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RxTxRouterProcess implements Runnable {

  private final static int DEFAULT_REQUEST_QUEUE_SIZE = 10;

  private RxTxConfiguration configuration;
  private RxTxRouter rxTxRouter;
  private boolean stopRequested;
  private BlockingQueue<SerialRequest> requestsQueue;
  private Semaphore routerAccessLock;

  @Builder
  public RxTxRouterProcess(
      @NonNull RxTxConfiguration configuration,
      @NonNull SerialPort serialPort,
      Consumer<ViewBuffer> callbackHandler,
      Consumer<ViewBuffer> responseHandler)  {
    this();
    this.configuration = configuration;
    this.rxTxRouter = RxTxRouter.builder()
        .configuration(configuration)
        .serialPort(serialPort)
        .responseHandler(responseHandler)
        .callbackHandler(callbackHandler)
        .build();
  }

  private RxTxRouterProcess() {
    this.requestsQueue = new ArrayBlockingQueue(DEFAULT_REQUEST_QUEUE_SIZE);
    this.routerAccessLock = new Semaphore(1);
  }

  public void scheduleRequest(SerialRequest request) {
    try {
      requestsQueue.put(request);
    } catch(InterruptedException e) {
      log.error("Unexpected exception when enqueuing serial request!", e);
      throw new FatalSerialException(e, "Unexpected exception when enqueuing serial request!");
    }
  }

  public void sendRequest(SerialRequest request) throws SerialException {
    try {
      routerAccessLock.acquireUninterruptibly();
      rxTxRouter.runUnlessRequestSent(request);
    } finally {
      routerAccessLock.release();
    }
  }

  public void stop() {
    this.stopRequested = true;
  }

  @Override
  public void run() {
    stopRequested = false;

    while (!stopRequested) {
      try {
        while (!stopRequested) {
          runOnce();
          Thread.sleep(configuration.getRouterPollDelay());
        }
      } catch (Exception e) {
        log.error("Unexpected exception occurred, trying to reconnect!", e);
        rxTxRouter.reconnectPort();
      }
    }
  }

  private void runOnce() throws SerialException {
    routerAccessLock.acquireUninterruptibly();
    try {
      if (!requestsQueue.isEmpty()) {
        try {
          SerialRequest nextRequest = requestsQueue.peek();
          rxTxRouter.runUnlessRequestSent(nextRequest);
        } catch (RequestFlowException e) {
          log.error("Failed to send request", e);
        }
      } else {
        rxTxRouter.runSingleCycle();
      }
    } finally {
      routerAccessLock.release();
    }
  }
}
