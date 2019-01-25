package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_NAK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_SOF;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_CATEGORY;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_COMMAND;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_RES;

import com.rposcro.jwavez.serial.exceptions.SerialStreamException;
import com.rposcro.jwavez.serial.utils.ViewBuffer;
import java.io.IOException;
import java.util.function.Consumer;
import lombok.Builder;

@Builder
public class ResponseStageDoer {

  private FrameInboundStream inboundStream;
  private FrameOutboundStream outboundStream;
  private RxTxConfiguration configuration;
  private Consumer<ViewBuffer> responseHandler;

  public ResponseStageResult acquireResponse(byte expectedCommand) throws IOException, SerialStreamException {
    long timeoutPoint = System.currentTimeMillis() + configuration.getResponseTimeout();
    ViewBuffer frameView;
    do {
      frameView = inboundStream.nextFrame();
      if (frameView.hasRemaining()) {
        return receiveFrame(frameView, expectedCommand);
      }
    } while (timeoutPoint > System.currentTimeMillis());
    return ResponseStageResult.RESULT_TIMEOUT;
  }

  private ResponseStageResult receiveFrame(ViewBuffer frameBuffer, byte expectedCommand) throws IOException {
    switch(frameBuffer.get(FRAME_OFFSET_CATEGORY)) {
      case CATEGORY_SOF:
        return receiveSOF(frameBuffer, expectedCommand);
      case CATEGORY_ACK:
      case CATEGORY_NAK:
      case CATEGORY_CAN:
        processException();
        return ResponseStageResult.RESULT_ODD_CATEGORY;
      default:
        processException();
        return ResponseStageResult.RESULT_ODD_INCOME;
    }
  }

  private ResponseStageResult receiveSOF(ViewBuffer frameBuffer, byte expectedCommand) throws IOException {
    if (frameBuffer.get(FRAME_OFFSET_TYPE) == TYPE_RES && frameBuffer.get(FRAME_OFFSET_COMMAND) == expectedCommand) {
      outboundStream.writeACK();
      responseHandler.accept(frameBuffer);
      return ResponseStageResult.RESULT_OK;
    } else {
      return ResponseStageResult.RESULT_DIVERGENT_RESPONSE;
    }
  }

  private void processException() throws IOException {
    outboundStream.writeCAN();
    inboundStream.purgeStream();
  }
}
