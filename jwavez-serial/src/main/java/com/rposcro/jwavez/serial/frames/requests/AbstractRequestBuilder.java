package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.serial.buffers.DisposableFrameBuffer;
import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

abstract class AbstractRequestBuilder {

    protected final int FRAME_CONTROL_SIZE = 5;

    protected DisposableFrameBuffer startUpFrameBuffer(int capacity, SerialCommand command) {
        return new DisposableFrameBuffer(capacity)
                .put(SerialFrameConstants.CATEGORY_SOF)
                .put((byte) (capacity - 2))
                .put(SerialFrameConstants.TYPE_REQ)
                .put(command.getCode());
    }

    protected SerialRequest nonPayloadRequest(SerialCommand command) {
        return SerialRequest.builder()
                .responseExpected(true)
                .frameData(completeFrameBuffer(command))
                .serialCommand(command)
                .build();
    }

    private FrameBuffer completeFrameBuffer(SerialCommand command) {
        return new DisposableFrameBuffer(5)
                .put(SerialFrameConstants.CATEGORY_SOF)
                .put((byte) (3))
                .put(SerialFrameConstants.TYPE_REQ)
                .put(command.getCode())
                .putCRC();
    }
}
