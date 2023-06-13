package com.rposcro.jwavez.serial.frames.callbacks;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.SEND_SUC_ID)
public class SendSUCIdCallback extends FlowCallback {

    private TransmitCompletionStatus txStatus;
    private boolean statusReportPresent;

    public SendSUCIdCallback(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.txStatus = TransmitCompletionStatus.ofCode(frameBuffer.nextByte());
        this.statusReportPresent = frameBuffer.available() > 1;
    }
}
