package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_NAK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_SOF;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_CATEGORY;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.exceptions.RxTxException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;

import lombok.Builder;

@Builder
public class RequestStageDoer {

    private FrameInboundStream inboundStream;
    private FrameOutboundStream outboundStream;
    private RxTxConfiguration configuration;

    public RequestStageResult sendRequest(ImmutableBuffer outboundBuffer) throws RxTxException {
        outboundStream.writeSOF(outboundBuffer);
        if (outboundBuffer.hasNext()) {
            return RequestStageResult.RESULT_ERR_OUTCOME;
        }
        return expectACK();
    }

    private RequestStageResult expectACK() throws RxTxException {
        long timeoutPoint = System.currentTimeMillis() + configuration.getFrameAckTimeout();
        do {
            ViewBuffer frameView = inboundStream.nextFrame();
            if (frameView.hasRemaining()) {
                switch (frameView.get(FRAME_OFFSET_CATEGORY)) {
                    case CATEGORY_ACK:
                        return RequestStageResult.RESULT_OK;
                    case CATEGORY_NAK:
                        return RequestStageResult.RESULT_NAK;
                    case CATEGORY_CAN:
                        return RequestStageResult.RESULT_CAN;
                    case CATEGORY_SOF:
                        processException();
                        return RequestStageResult.RESULT_SOF;
                    default:
                        processException();
                        return RequestStageResult.RESULT_ODD_INCOME;
                }
            }
        } while (timeoutPoint > System.currentTimeMillis());

        return RequestStageResult.RESULT_ACK_TIMEOUT;
    }

    private void processException() throws SerialPortException {
        outboundStream.writeCAN();
        inboundStream.purgeStream();
    }
}
