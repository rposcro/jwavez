package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_SOF;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_CATEGORY;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_REQ;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.exceptions.RxTxException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class IdleStageDoer {

    private FrameInboundStream inboundStream;
    private FrameOutboundStream outboundStream;
    private CallbackHandler callbackHandler;

    public IdleStageResult checkInbound() throws RxTxException {
        ImmutableBuffer frameBuffer = inboundStream.nextFrame();
        if (!framePresent(frameBuffer)) {
            return IdleStageResult.RESULT_SILENCE;
        }
        return consumeFrame(frameBuffer);
    }

    private IdleStageResult consumeFrame(ImmutableBuffer frameBuffer) throws SerialPortException {
        IdleStageResult result;

        if (frameBuffer.getByte(FRAME_OFFSET_CATEGORY) == CATEGORY_SOF && frameBuffer.getByte(FRAME_OFFSET_TYPE) == TYPE_REQ) {
            outboundStream.writeACK();
            try {
                callbackHandler.accept(frameBuffer);
            } catch (Throwable t) {
                log.error("Callback handler thrown forbidden exception!", t);
            }
            result = IdleStageResult.RESULT_CALLBACK_HANDLED;
        } else {
            processException();
            result = IdleStageResult.RESULT_ODD_INCOME;
        }

        frameBuffer.dispose();
        return result;
    }

    private boolean framePresent(ImmutableBuffer frameBuffer) {
        return frameBuffer.hasNext();
    }

    private void processException() throws SerialPortException {
        outboundStream.writeCAN();
        inboundStream.purgeStream();
    }
}
