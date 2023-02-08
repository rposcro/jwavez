package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_NAK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_SOF;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_CATEGORY;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_COMMAND;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_RES;

import com.rposcro.jwavez.serial.exceptions.RxTxException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;

import java.util.function.Consumer;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class ResponseStageDoer {

    private FrameInboundStream inboundStream;
    private FrameOutboundStream outboundStream;
    private RxTxConfiguration configuration;
    private ResponseHandler responseHandler;

    public ResponseStageResult acquireResponse(byte expectedCommand) throws RxTxException {
        long timeoutPoint = System.currentTimeMillis() + configuration.getFrameResponseTimeout();
        ViewBuffer frameView;
        do {
            frameView = inboundStream.nextFrame();
            if (frameView.hasRemaining()) {
                return receiveFrame(frameView, expectedCommand);
            }
        } while (timeoutPoint > System.currentTimeMillis());
        return ResponseStageResult.RESULT_RESPONSE_TIMEOUT;
    }

    private ResponseStageResult receiveFrame(ViewBuffer frameBuffer, byte expectedCommand) throws SerialPortException {
        switch (frameBuffer.get(FRAME_OFFSET_CATEGORY)) {
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

    private ResponseStageResult receiveSOF(ViewBuffer frameBuffer, byte expectedCommand) throws SerialPortException {
        if (frameBuffer.get(FRAME_OFFSET_TYPE) == TYPE_RES && frameBuffer.get(FRAME_OFFSET_COMMAND) == expectedCommand) {
            outboundStream.writeACK();
            try {
                responseHandler.accept(frameBuffer);
            } catch (Throwable t) {
                log.error("Response handler thrown forbidden exception!", t);
            }
            return ResponseStageResult.RESULT_OK;
        } else {
            processException();
            return ResponseStageResult.RESULT_DIVERGENT_RESPONSE;
        }
    }

    private void processException() throws SerialPortException {
        outboundStream.writeCAN();
        inboundStream.purgeStream();
    }
}
