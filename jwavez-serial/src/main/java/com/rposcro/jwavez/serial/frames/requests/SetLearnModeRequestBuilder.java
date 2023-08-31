package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SET_LEARN_MODE;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.model.LearnMode;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class SetLearnModeRequestBuilder extends AbstractRequestBuilder {

    public SetLearnModeRequestBuilder(ByteBufferManager byteBufferManager) {
        super(byteBufferManager);
    }

    public SerialRequest createSetLearnModeRequest(LearnMode learnMode, byte callbackFlowId) {
        ImmutableBuffer buffer = dataBuilder(SET_LEARN_MODE, 2)
                .add(learnMode.getCode())
                .add(callbackFlowId)
                .build();
        return SerialRequest.builder()
                .responseExpected(false)
                .serialCommand(SET_LEARN_MODE)
                .frameData(buffer)
                .callbackFlowId(callbackFlowId)
                .build();
    }
}
