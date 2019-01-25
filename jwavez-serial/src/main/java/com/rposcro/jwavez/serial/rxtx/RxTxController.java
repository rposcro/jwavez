package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_COMMAND;
import static java.lang.System.currentTimeMillis;

import com.rposcro.jwavez.serial.exceptions.RequestFlowException;
import com.rposcro.jwavez.serial.exceptions.SerialStreamException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import lombok.Builder;

@Builder
public class RxTxController {

  private static final long POLL_FREQUENCY_MILLIS = 50;

  private RxTxConfiguration configuration;
  private RequestStageDoer requestStageDoer;
  private ResponseStageDoer responseStageDoer;
  private IdleStageDoer idleStageDoer;

  private final Semaphore outboundLock;
  private final CompletableFuture<?> futureResponse;

  private FrameRequest frameRequest;
  private int retransmissionCounter;
  private long retransmissionTime;

  private RxTxController() {
    this.futureResponse = new CompletableFuture<>();
    this.outboundLock = new Semaphore(1);
  }

  public <T> T sendFrame(FrameRequest frameRequest) throws SerialStreamException {
    outboundLock.acquireUninterruptibly();
    try {
      this.frameRequest = frameRequest;
      this.retransmissionCounter = 0;
      this.retransmissionTime = currentTimeMillis();
      return (T) futureResponse.get();
    } catch(CancellationException | InterruptedException | ExecutionException e) {
      throw new SerialStreamException(e);
    } finally {
      outboundLock.release();
    }
  }

  public void run() {
    try {
      receiveStage();
      transmitStage();
      Thread.sleep(POLL_FREQUENCY_MILLIS);
    } catch(Exception e) {

    }
  }

  private void receiveStage() throws IOException, SerialStreamException {
    IdleStageResult idleStageResult;
    do {
      idleStageResult = idleStageDoer.checkInbound();
    } while(idleStageResult != IdleStageResult.RESULT_SILENCE);
  }

  private void transmitStage() throws IOException, SerialStreamException {
    if (retransmissionTime <= currentTimeMillis() && frameRequest != null) {
      ByteBuffer frameData = frameRequest.getFrameData();
      RequestStageResult reqResult = requestStageDoer.sendRequest(frameData);
      boolean success = false;

      if (reqResult == RequestStageResult.RESULT_OK) {
        if (frameRequest.isResponseExpected()) {
          byte commandCode = frameData.get(FRAME_OFFSET_COMMAND);
          ResponseStageResult resResult = responseStageDoer.acquireResponse(commandCode);
          success = resResult == ResponseStageResult.RESULT_OK;
        } else {
          futureResponse.complete(null);
          success = true;
        }
      }

      if (!success) {
        pursueRetransmission();
      }
    }
  }

  private void pursueRetransmission() {
    if (frameRequest.isRetransmissionDisabled() || ++retransmissionCounter > configuration.getMaxRetriesCount()) {
      futureResponse.completeExceptionally(new RequestFlowException("Failed to transmit frame"));
    } else {
      retransmissionTime = currentTimeMillis() + retransmissionDelay(retransmissionCounter);
    }
  }

  private long retransmissionDelay(int retryNumber) {
    return configuration.getRetryDelayBias() + (configuration.getRetryDelayFactor() * retryNumber);
  }
}
