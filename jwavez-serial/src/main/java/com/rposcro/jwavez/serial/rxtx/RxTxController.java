package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_COMMAND;
import static com.rposcro.jwavez.serial.utils.BufferUtil.bufferToString;
import static java.lang.System.currentTimeMillis;

import com.rposcro.jwavez.serial.exceptions.FatalSerialException;
import com.rposcro.jwavez.serial.exceptions.FrameTimeoutException;
import com.rposcro.jwavez.serial.exceptions.OddFrameException;
import com.rposcro.jwavez.serial.exceptions.RequestFlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.exceptions.SerialStreamException;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import com.rposcro.jwavez.serial.utils.ViewBuffer;
import java.nio.ByteBuffer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RxTxController {

  private static final long POLL_FREQUENCY_MILLIS = 50;

  private SerialPort serialPort;
  private RxTxConfiguration configuration;
  private IdleStageDoer idleStageDoer;
  private RequestStageDoer requestStageDoer;
  private ResponseStageDoer responseStageDoer;
  private FrameInboundStream inboundStream;
  private FrameOutboundStream outboundStream;

  private final Semaphore outboundLock;
  private final CompletableFuture<?> futureResponse;

  private FrameRequest frameRequest;
  private int retransmissionCounter;
  private long retransmissionTime;

  @Builder
  public RxTxController(
      RxTxConfiguration configuration,
      SerialPort serialPort,
      Consumer<ViewBuffer> responseHandler,
      Consumer<ViewBuffer> callbackHandler)  {
    this();
    this.configuration = configuration;
    this.serialPort = serialPort;

    this.inboundStream = FrameInboundStream.builder()
        .configuration(configuration)
        .serialPort(serialPort)
        .build();
    this.outboundStream = FrameOutboundStream.builder()
        .serialPort(serialPort)
        .build();

    this.idleStageDoer = IdleStageDoer.builder()
        .inboundStream(inboundStream)
        .outboundStream(outboundStream)
        .callbackHandler(callbackHandler == null ?  this::handleCallback : callbackHandler)
        .build();
    this.requestStageDoer = RequestStageDoer.builder()
        .inboundStream(inboundStream)
        .outboundStream(outboundStream)
        .configuration(configuration)
        .build();
    this.responseStageDoer = ResponseStageDoer.builder()
        .inboundStream(inboundStream)
        .outboundStream(outboundStream)
        .configuration(configuration)
        .responseHandler(responseHandler == null ? this::handleResponse : responseHandler)
        .build();
  }

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
    while (true) {
      try {
        while (true) {
          try {
            receiveStage();
            transmitStage();
            Thread.sleep(POLL_FREQUENCY_MILLIS);
          } catch (OddFrameException e) {
            outboundStream.writeNAK();
            inboundStream.purgeStream();
          } catch (FrameTimeoutException e) {
            outboundStream.writeCAN();
            inboundStream.purgeStream();
          } catch (SerialStreamException e) {
            outboundStream.writeCAN();
            inboundStream.purgeStream();
          }
        }
      } catch (Exception e) {
        log.error("Unexpected exception occurred, trying to reconnect!", e);
        reconnectPort();
      }
    }
  }

  private void reconnectPort() {
    int retryCount = 0;
    while (retryCount++ < configuration.getPortReconnectMaxCount()) {
      try {
        Thread.sleep(reconnectDelay(retryCount));
        serialPort.reconnect();
        inboundStream.purgeStream();
        return;
      } catch(SerialPortException e) {
        log.error("Port reconnection failed!", e);
      } catch(InterruptedException e) {
      }
    }
    throw new FatalSerialException("Reconnection to serial port failed after %s retries!", retryCount);
  }

  private void receiveStage() throws SerialException {
    IdleStageResult idleStageResult;
    do {
      idleStageResult = idleStageDoer.checkInbound();
    } while(idleStageResult != IdleStageResult.RESULT_SILENCE);
  }

  private void transmitStage() throws SerialException {
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
    if (frameRequest.isRetransmissionDisabled() || ++retransmissionCounter > configuration.getRequestRetriesMaxCount()) {
      futureResponse.completeExceptionally(new RequestFlowException("Failed to transmit frame"));
    } else {
      retransmissionTime = currentTimeMillis() + retransmissionDelay(retransmissionCounter);
    }
  }

  private long retransmissionDelay(int retryNumber) {
    return configuration.getRequestRetryDelayBias() + (configuration.getRequestRetryDelayFactor() * retryNumber);
  }

  private long reconnectDelay(int retryNumber) {
    return configuration.getPortReconnectDelayBias() + (configuration.getPortReconnectDelayFactor() * retryNumber);
  }

  private void handleResponse(ViewBuffer frameView) {
    log.info("Response frame received: " + bufferToString(frameView));
  }

  private void handleCallback(ViewBuffer frameView) {
    log.info("Callback frame received: " + bufferToString(frameView));
  }
}
