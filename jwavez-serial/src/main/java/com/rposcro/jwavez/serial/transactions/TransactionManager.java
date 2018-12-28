package com.rposcro.jwavez.serial.transactions;

import com.rposcro.jwavez.serial.exceptions.TransactionException;
import com.rposcro.jwavez.serial.frame.constants.FrameType;
import com.rposcro.jwavez.serial.frame.SOFCallbackFrame;
import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptor;
import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptorContext;
import com.rposcro.jwavez.serial.rxtx.OutboundOrder;
import com.rposcro.jwavez.serial.rxtx.OutboundResult;
import com.rposcro.jwavez.serial.rxtx.SerialCommunicationBroker;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionManager implements InboundFrameInterceptor {

  private static final long TIMEOUT_RESPONSE = 65000;

  private TransactionIdDispatcher callbackIdDispatcher;
  private SerialCommunicationBroker communicationBroker;

  private ConcurrentHashMap<TransactionId, TransactionContext> transactionsPerId;
  private LinkedBlockingQueue<TransactionContext> awaitingForLaunch;
  private Optional<TransactionContext> awaitingResponse;
  private CountDownLatch awaitingResponseLatch;

  @Builder
  public TransactionManager(
      TransactionIdDispatcher callbackIdDispatcher,
      SerialCommunicationBroker communicationBroker) {
    this.communicationBroker = communicationBroker;
    this.callbackIdDispatcher = callbackIdDispatcher;
    this.transactionsPerId = new ConcurrentHashMap<>();
    this.awaitingForLaunch = new LinkedBlockingQueue<>();
    this.awaitingResponse = Optional.empty();
    startOutboundResultThread();
    startTransactionLaunchThread();
  }

  public <T> Future<TransactionResult<T>> scheduleTransaction(SerialTransaction transaction) throws TransactionException {
    TransactionId transactionId = callbackIdDispatcher.acquireId();
    TransactionContext transactionContext = transaction.init(transactionId);
    awaitingForLaunch.add(transactionContext);
    return transactionContext.getFutureResult();
  }

  @Override
  public void intercept(InboundFrameInterceptorContext context) {
    SOFFrame inboundFrame = context.getFrame();
    if (inboundFrame.getFrameType() == FrameType.RES) {
      acceptResponseFrame((SOFResponseFrame) inboundFrame);
    } else if (inboundFrame.getFrameType() == FrameType.REQ) {
      acceptCallbackFrame((SOFCallbackFrame) inboundFrame);
    }
  }

  private void launchTransaction(TransactionContext<?> transactionContext) throws InterruptedException {
    SerialTransaction transaction = transactionContext.getTransaction();
    TransactionId transactionId = transactionContext.getTransactionId();
    transactionsPerId.put(transactionId, transactionContext);
    SOFRequestFrame startupFrame = transaction.startUp();
    enqueuOutboundOrder(startupFrame, transactionId);

    if (transaction.expectsResponse()) {
      awaitingResponse = Optional.of(transactionContext);
      awaitingResponseLatch = new CountDownLatch(1);
      if (!awaitingResponseLatch.await(TIMEOUT_RESPONSE, TimeUnit.MILLISECONDS)) {
        log.error("Awaiting for response frame timed out!");
        transaction.timeoutOccurred();
      }
    }
    challengeCalledBackTransactionToClose(transactionContext);
  }

  private void acceptResponseFrame(SOFResponseFrame responseFrame) {
    if (awaitingResponse.isPresent()) {
      TransactionContext transactionContext = awaitingResponse.get();
      Optional<SOFFrame> nextFrame = transactionContext.getTransaction().acceptInboundFrame(responseFrame);
      processNextStep(nextFrame, transactionContext);
      if (awaitingResponseLatch != null) {
        awaitingResponseLatch.countDown();
      }
      awaitingResponseLatch = null;
      awaitingResponse = Optional.empty();
    } else {
      log.warn("Response frame received while haven't expected one, frame skipped!");
    }
  }

  private void acceptCallbackFrame(SOFCallbackFrame callbackFrame) {
    byte callbackId = callbackFrame.getCallbackFunctionId();

    if (callbackId > 0) {
      TransactionId transactionId = callbackIdDispatcher.activeForCallbackId(callbackId);
      TransactionContext transactionContext = Optional.ofNullable(transactionId)
          .map(transactionsPerId::get)
          .orElse(null);

      if (transactionContext != null) {
        Optional<SOFFrame> nextFrame = transactionContext.getTransaction().acceptInboundFrame(callbackFrame);
        processNextStep(nextFrame, transactionContext);
      } else {
        log.warn("No transaction with callback id {}, skipped!", callbackId);
      }
    } else {
      log.info("No callback id in frame, skipped");
    }
  }

  private void processNextStep(Optional<SOFFrame> nextFrame, TransactionContext<?> transactionContext) {
    nextFrame.ifPresent(frame -> enqueuOutboundOrder(frame, transactionContext.getTransactionId()));
    challengeCalledBackTransactionToClose(transactionContext);
  }

  private void startTransactionLaunchThread() {
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          while (!isInterrupted()) {
            TransactionContext context = awaitingForLaunch.take();
            launchTransaction(context);
          }
        } catch(InterruptedException e) {
          log.warn("Transaction launch thread interrupted", e);
        }
      }
    };

    thread.setDaemon(true);
    thread.start();
  }

  private void enqueuOutboundOrder(SOFFrame frame, TransactionId transactionId) {
    OutboundOrder order = new OutboundOrder(frame, Optional.ofNullable(transactionId));
    communicationBroker.enqueueOutboundOrder(order);
  }

  private void startOutboundResultThread() {
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          while (!isInterrupted()) {
            OutboundResult outboundResult = communicationBroker.takeOutboundResult();
            TransactionId transactionId = (TransactionId) outboundResult.getOrderMarker().get();
            TransactionContext transactionContext = transactionsPerId.get(transactionId);

            if (transactionContext != null) {
              if (outboundResult.isSuccess()) {
                transactionContext.getTransaction().deliverySuccessful();
              } else {
                transactionContext.getTransaction().deliveryFailed();
              }
              challengeCalledBackTransactionToClose(transactionContext);
            } else {
              log.warn("Received order result for non-existing transaction {}", transactionId);
            }
          }
        } catch(InterruptedException e) {
          log.warn("Outbound result thread interrupted!");
        }
      }
    };
    thread.setDaemon(true);
    thread.start();
  }

  private void challengeCalledBackTransactionToClose(TransactionContext context) {
    if (!context.isActive()) {
      transactionsPerId.remove(context.getTransactionId());
      callbackIdDispatcher.releaseId(context.getTransactionId());
    }
  }
}
