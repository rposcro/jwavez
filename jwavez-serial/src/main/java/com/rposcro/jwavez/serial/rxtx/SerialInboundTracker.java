package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.frame.constants.FrameCategory;
import com.rposcro.jwavez.serial.utils.ByteBuffer;
import java.io.IOException;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@AllArgsConstructor
public class SerialInboundTracker implements Runnable {

  private static final long INPUT_POOLING_RATE_MS = 10;

  @Getter
  private SerialReceiver serialReceiver;

  private Consumer<ByteBuffer> ackHandler = this::handleFrame;
  private Consumer<ByteBuffer> nakHandler = this::handleFrame;
  private Consumer<ByteBuffer> canHandler = this::handleFrame;
  private Consumer<ByteBuffer> sofHandler = this::handleFrame;

  public void bindAckHandler(@NonNull Consumer<ByteBuffer> handler) {
    this.ackHandler = handler;
  }

  public void bindNakHandler(@NonNull Consumer<ByteBuffer> handler) {
    this.nakHandler = handler;
  }

  public void bindCanHandler(@NonNull Consumer<ByteBuffer> handler) {
    this.canHandler = handler;
  }

  public void bindSofHandler(@NonNull Consumer<ByteBuffer> handler) {
    this.sofHandler = handler;
  }

  public void run() {
    log.info("Serial inbound tracker started");
    try {
      while (true) {
        try {
          if (serialReceiver.dataAvailable()) {
            log.debug("Incoming data detected");
            ByteBuffer buffer = serialReceiver.receiveData();
            fireHandler(buffer);
          }
        } catch (Exception e) {
          log.error("Failed to receive frame from stream!", e);
          try {
            serialReceiver.purgeStream();
          } catch(IOException ex) {
            log.error("Exception while purging stream!", e);
          }
        }
        Thread.sleep(INPUT_POOLING_RATE_MS);
      }
    } catch(InterruptedException e) {
      log.info("Inbound thread sleep interrupted! {}", e.getMessage());
    }
  }

  private void fireHandler(ByteBuffer buffer) {
    FrameCategory category = FrameCategory.ofCode(buffer.get());

    switch(category) {
      case ACK:
        ackHandler.accept(buffer);
        break;
      case NAK:
        nakHandler.accept(buffer);
        break;
      case CAN:
        canHandler.accept(buffer);
        break;
      case SOF:
        sofHandler.accept(buffer);
        break;
      default:
        log.error("Unrecognized frame dropped");
    }
  }

  private void handleFrame(ByteBuffer buffer) {
    log.info("No active listener, dropping frame");
  }
}
