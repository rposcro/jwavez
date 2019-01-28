package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_SOF;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_CATEGORY;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_REQ;

import com.rposcro.jwavez.serial.exceptions.FrameTimeoutException;
import com.rposcro.jwavez.serial.exceptions.OddFrameException;
import com.rposcro.jwavez.serial.exceptions.SerialStreamException;
import com.rposcro.jwavez.serial.utils.ViewBuffer;
import java.io.IOException;
import java.util.function.Consumer;
import lombok.Builder;

@Builder
public class IdleStageDoer {

  private FrameInboundStream inboundStream;
  private FrameOutboundStream outboundStream;
  private Consumer<ViewBuffer> callbackHandler;

  public IdleStageResult checkInbound() throws IOException, SerialStreamException {
    ViewBuffer frameView = inboundStream.nextFrame();
    if (!framePresent(frameView)) {
      return IdleStageResult.RESULT_SILENCE;
    }
    return consumeFrame(frameView);
  }

  private IdleStageResult consumeFrame(ViewBuffer frameView) throws IOException {
    if (frameView.get(FRAME_OFFSET_CATEGORY) == CATEGORY_SOF && frameView.get(FRAME_OFFSET_TYPE) == TYPE_REQ) {
      outboundStream.writeACK();
      callbackHandler.accept(frameView);
      return IdleStageResult.RESULT_HANDLED;
    } else {
      processException();
      return IdleStageResult.RESULT_ODD_INCOME;
    }
  }

  private boolean framePresent(ViewBuffer frameBuffer) {
    return frameBuffer.hasRemaining();
  }

  private void processException() throws IOException {
    outboundStream.writeCAN();
    inboundStream.purgeStream();
  }
}
