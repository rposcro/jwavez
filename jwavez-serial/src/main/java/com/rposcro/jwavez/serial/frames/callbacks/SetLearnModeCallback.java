package com.rposcro.jwavez.serial.frames.callbacks;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.LearnStatus;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.SET_LEARN_MODE)
public class SetLearnModeCallback extends FlowCallback {

    private LearnStatus learnStatus;
    private NodeId nodeId;

    public SetLearnModeCallback(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.learnStatus = LearnStatus.ofCode(frameBuffer.nextByte());
        this.nodeId = new NodeId(frameBuffer.nextByte());
    }
}
