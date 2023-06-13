package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.buffer.ImmutableBufferBuilder;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.SerialFrameDataBuilder;

abstract class AbstractRequestBuilder {

    private static final int FRAME_CONTROL_SIZE = 5;
    private static final int NO_PAYLOAD_FRAME_SIZE = 3;

    private final ByteBufferManager byteBufferManager;

    protected AbstractRequestBuilder(ByteBufferManager byteBufferManager) {
        this.byteBufferManager = byteBufferManager;
    }

    protected ImmutableBufferBuilder dataBuilder(SerialCommand serialCommand, int payloadSize) {
        return new SerialFrameDataBuilder(byteBufferManager, FRAME_CONTROL_SIZE + payloadSize)
                .add(SerialFrameConstants.CATEGORY_SOF)
                .add((byte) (payloadSize - 2))
                .add(SerialFrameConstants.TYPE_REQ)
                .add(serialCommand.getCode());
    }

    protected SerialRequest nonPayloadRequest(SerialCommand command) {
        return SerialRequest.builder()
                .responseExpected(true)
                .frameData(completeFrameBuffer(command))
                .serialCommand(command)
                .build();
    }

    private ImmutableBuffer completeFrameBuffer(SerialCommand serialCommand) {
        return dataBuilder(serialCommand, 0)
                .add(SerialFrameConstants.CATEGORY_SOF)
                .add((byte) (NO_PAYLOAD_FRAME_SIZE))
                .add(SerialFrameConstants.TYPE_REQ)
                .add(serialCommand.getCode())
                .build();
    }
}
