package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SERIAL_API_SETUP;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.FieldUtil;

public class SerialAPISetupRequest extends ZWaveRequest {

    public static SerialRequest createSerialAPISetupRequest(boolean txStatusReportEnabled) {
        return SerialRequest.builder()
                .responseExpected(true)
                .serialCommand(SERIAL_API_SETUP)
                .frameData(startUpFrameBuffer(FRAME_CONTROL_SIZE + 1, SERIAL_API_SETUP)
                        .put(FieldUtil.booleanByte(txStatusReportEnabled))
                        .putCRC())
                .build();
    }

    public static SerialRequest createEnableStatusReportRequest() {
        return createSerialAPISetupRequest(true);
    }

    public static SerialRequest createDisableStatusReportRequest() {
        return createSerialAPISetupRequest(false);
    }
}