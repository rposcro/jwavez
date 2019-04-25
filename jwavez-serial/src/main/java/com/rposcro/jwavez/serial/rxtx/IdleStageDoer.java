package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_SOF;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_CATEGORY;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_REQ;

import com.rposcro.jwavez.serial.exceptions.RxTxException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import java.util.function.Consumer;
import lombok.Builder;

@Builder
public class IdleStageDoer {

  private FrameInboundStream inboundStream;
  private FrameOutboundStream outboundStream;
  private Consumer<ViewBuffer> callbackHandler;

  public IdleStageResult checkInbound() throws RxTxException {
    ViewBuffer frameView = inboundStream.nextFrame();
    if (!framePresent(frameView)) {
      return IdleStageResult.RESULT_SILENCE;
    }
    return consumeFrame(frameView);
  }

  private IdleStageResult consumeFrame(ViewBuffer frameView) throws SerialPortException {
    if (frameView.get(FRAME_OFFSET_CATEGORY) == CATEGORY_SOF && frameView.get(FRAME_OFFSET_TYPE) == TYPE_REQ) {
      outboundStream.writeACK();
      callbackHandler.accept(frameView);
      return IdleStageResult.RESULT_CALLBACK_HANDLED;
    } else {
      processException();
      return IdleStageResult.RESULT_ODD_INCOME;
    }
  }

  private boolean framePresent(ViewBuffer frameBuffer) {
    return frameBuffer.hasRemaining();
  }

  private void processException() throws SerialPortException {
    outboundStream.writeCAN();
    inboundStream.purgeStream();
  }
}
