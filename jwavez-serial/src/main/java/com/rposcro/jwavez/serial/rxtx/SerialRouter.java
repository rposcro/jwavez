package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.frame.SerialFrame.*;

import com.rposcro.jwavez.serial.exceptions.CommunicationException;
import com.rposcro.jwavez.serial.exceptions.FrameException;
import com.rposcro.jwavez.serial.frame.SOFFrameParser;
import com.rposcro.jwavez.serial.frame.SOFFrameValidator;
import com.rposcro.jwavez.serial.utils.ByteBuffer;
import com.rposcro.jwavez.serial.utils.TimeoutKeeper;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialRouter {

  private static final long TIMEOUT_ACK = 1600;

  @Getter
  private SerialCommunicationBroker communicationBroker;
  @Getter
  private SerialTransmitter transmitter;
  @Getter
  private SOFFrameParser frameParser;
  @Getter
  private SOFFrameValidator frameValidator;

  private CompletableFuture<Boolean> transmissionResult;
  private TimeoutKeeper timeoutKeeper;

  private final Semaphore controlLock = new Semaphore(1);

  @Builder
  private SerialRouter(
      SerialTransmitter transmitter,
      SOFFrameParser frameParser,
      SOFFrameValidator frameValidator,
      SerialCommunicationBroker communicationBroker) {
    this.transmitter = transmitter;
    this.frameParser = frameParser;
    this.frameValidator = frameValidator;
    this.communicationBroker = communicationBroker;
  }

  public void handleOutboundOrder(OutboundOrder order) {
    boolean result = false;
    controlLock.acquireUninterruptibly();
    try {
      CompletableFuture<Boolean> timeoutResult = trackACK();
      transmitter.transmitData(order.getOutboundFrame().getBuffer());
      result = timeoutResult.get();
    } catch(Exception e) {
      log.error("Error sending outbound frame: {}", e.getMessage());
    } finally {
      controlLock.release();
      communicationBroker.enqueueOutboundResult(new OutboundResult(order.getOrderMarker(), result));
    }
  }

  public void handleInboundACK(ByteBuffer buffer) {
    cancelACKTracking(true);
  }

  public void handleInboundNAK(ByteBuffer buffer) {
    cancelACKTracking(false);
    log.warn("Frame denied by NAK");
  }

  public void handleInboundCAN(ByteBuffer buffer) {
    cancelACKTracking(false);
    log.warn("Frame denied by CAN");
  }

  public void handleInboundSOF(ByteBuffer buffer) {
    controlLock.acquireUninterruptibly();
    try {
      if (!frameValidator.validate(buffer)) {
        throw new FrameException("Frame validation error");
      } else {
        transmitter.transmitData(ACK_FRAME.getBuffer());
        communicationBroker.enqueueInboundFrame(frameParser.parseFrame(buffer.cloneArray()));
      }
    } catch (FrameException e) {
      log.warn("Invalid SOF received, sending CAN", e);
      try {
        transmitter.transmitData(CAN_FRAME.getBuffer());
      } catch(IOException ex) {
        log.error("Failed to send CAN frame", ex);
      }
    } catch (IOException e) {
      log.error("Failed to send ACK frame", e);
    } finally {
      controlLock.release();
    }
  }

  private CompletableFuture<Boolean> trackACK() throws CommunicationException {
    timeoutKeeper = TimeoutKeeper.setTimeout(TIMEOUT_ACK, this::handleTimeout);
    transmissionResult = new CompletableFuture<>();
    return transmissionResult;
  }

  private boolean cancelACKTracking(boolean result) {
    if (timeoutKeeper != null) {
      timeoutKeeper.cancel();
      transmissionResult.complete(result);
      timeoutKeeper = null;
      transmissionResult = null;
      return true;
    } else {
      log.warn("Unexpected control frame");
      return false;
    }
  }

  private void handleTimeout() {
    cancelACKTracking(false);
  }
}
