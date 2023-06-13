package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.buffer.ImmutableBufferBuilder;
import com.rposcro.jwavez.serial.model.ApiSetupSubCommand;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SERIAL_API_SETUP;
import static com.rposcro.jwavez.serial.enums.SerialCommand.SET_DEFAULT;

public class DeviceManagementRequestBuilder extends AbstractRequestBuilder {

    public DeviceManagementRequestBuilder(ByteBufferManager byteBufferManager) {
        super(byteBufferManager);
    }

    public SerialRequest createSetDefaultRequest(byte sessionId) {
        ImmutableBuffer buffer = dataBuilder(SET_DEFAULT, 1)
                .add(sessionId)
                .build();
        return SerialRequest.builder()
                .responseExpected(false)
                .serialCommand(SET_DEFAULT)
                .frameData(buffer)
                .callbackFlowId(sessionId)
                .build();
    }

    public SerialRequest createSerialAPISetupRequest(ApiSetupSubCommand subCommand, byte... subCommandPayload) {
        ImmutableBufferBuilder bufferBuilder = dataBuilder(SERIAL_API_SETUP, 2 + subCommandPayload.length)
                .add(subCommand.getCode());

        for (byte bt : subCommandPayload) {
            bufferBuilder.add(bt);
        }

        return SerialRequest.builder()
                .responseExpected(true)
                .serialCommand(SERIAL_API_SETUP)
                .frameData(bufferBuilder.build())
                .build();
    }
}
