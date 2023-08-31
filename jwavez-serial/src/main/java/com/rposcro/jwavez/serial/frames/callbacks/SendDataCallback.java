package com.rposcro.jwavez.serial.frames.callbacks;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.SEND_DATA)
public class SendDataCallback extends FlowCallback {

    private TransmitCompletionStatus transmitCompletionStatus;
    private boolean statusReportPresent;

    public SendDataCallback(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.transmitCompletionStatus = TransmitCompletionStatus.ofCode(frameBuffer.nextByte());
        this.statusReportPresent = frameBuffer.available() > 1;
    }

    @Override
    public String asFineString() {
        return String.format("%s(%02x) clbckId(%02x) %s(%02x)",
                getSerialCommand().name(), getSerialCommand().getCode(), getCallbackFlowId(),
                transmitCompletionStatus, transmitCompletionStatus.getCode());
    }
}
