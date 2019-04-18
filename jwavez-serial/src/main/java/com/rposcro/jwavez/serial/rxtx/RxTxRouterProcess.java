package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.core.utils.ObjectsUtil;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.exceptions.FatalSerialException;
import com.rposcro.jwavez.serial.exceptions.RequestFlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import com.rposcro.jwavez.serial.utils.async.Runner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
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
      @NonNull SerialPort serialPort,
      RxTxConfiguration configuration,
      Consumer<ViewBuffer> callbackHandler,
      Consumer<ViewBuffer> responseHandler)  {
    this();
    this.configuration = ObjectsUtil.orDefault(configuration, RxTxConfiguration::defaultConfiguration);
    this.rxTxRouter = RxTxRouter.builder()
        .configuration(this.configuration)
        .serialPort(serialPort)
        .responseHandler(responseHandler)
        .callbackHandler(callbackHandler)
        .build();
  }

  RxTxRouterProcess() {
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
    executeSynchronous(() -> rxTxRouter.runUnlessRequestSent(request));
  }

  public void stop() {
    this.stopRequested = true;
  }

  public void resetStreams() throws SerialException {
    executeSynchronous(rxTxRouter::purgeInput);
  }

  @Override
  public void run() {
    stopRequested = false;

    while (!stopRequested) {
      try {
        while (!stopRequested) {
          executeSynchronous(this::runOnce);
          Thread.sleep(configuration.getRouterPollDelay());
        }
      } catch (Exception e) {
        log.error("Unexpected exception occurred, trying to reconnect!", e);
        rxTxRouter.reconnectPort();
      }
    }
  }

  private void runOnce() throws SerialException {
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
  }

  private void executeSynchronous(Runner<SerialException> runner) throws SerialException {
    try {
      routerAccessLock.acquireUninterruptibly();
      runner.run();
    } finally {
      routerAccessLock.release();
    }
  }
}
