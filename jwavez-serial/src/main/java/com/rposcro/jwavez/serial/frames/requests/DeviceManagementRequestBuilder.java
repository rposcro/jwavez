package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.serial.buffers.DisposableFrameBuffer;
import com.rposcro.jwavez.serial.model.ApiSetupSubCommand;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SERIAL_API_SETUP;
import static com.rposcro.jwavez.serial.enums.SerialCommand.SET_DEFAULT;

public class DeviceManagementRequestBuilder extends AbstractRequestBuilder {

    public SerialRequest createSetDefaultRequest(byte sessionId) {
        return SerialRequest.builder()
                .responseExpected(false)
                .serialCommand(SET_DEFAULT)
                .frameData(startUpFrameBuffer(FRAME_CONTROL_SIZE + 1, SET_DEFAULT)
                        .put(sessionId)
                        .putCRC())
                .callbackFlowId(sessionId)
                .build();
    }

    public SerialRequest createSerialAPISetupRequest(ApiSetupSubCommand subCommand, byte... subCommandPayload) {
        DisposableFrameBuffer buffer = startUpFrameBuffer(
                FRAME_CONTROL_SIZE + 2 + subCommandPayload.length, SERIAL_API_SETUP)
                .put(subCommand.getCode());
        for (byte bt : subCommandPayload) {
            buffer.put(bt);
        }
        buffer.putCRC();

        return SerialRequest.builder()
                .responseExpected(true)
                .serialCommand(SERIAL_API_SETUP)
                .frameData(buffer)
                .build();
    }
}
