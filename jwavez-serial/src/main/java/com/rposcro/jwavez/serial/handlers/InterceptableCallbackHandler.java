package com.rposcro.jwavez.serial.handlers;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_REQ;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.InboundFrameValidator;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.interceptors.CallbackInterceptor;
import com.rposcro.jwavez.serial.interceptors.FrameBufferInterceptor;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InterceptableCallbackHandler implements CallbackHandler {

    private InboundFrameValidator validator;
    private InboundFrameParser parser;

    private List<CallbackInterceptor> callbackInterceptors;
    private List<FrameBufferInterceptor> bufferInterceptors;

    public InterceptableCallbackHandler() {
        this.validator = new InboundFrameValidator();
        this.parser = new InboundFrameParser();
        this.callbackInterceptors = new ArrayList<>();
        this.bufferInterceptors = new ArrayList<>();
    }

    @Override
    public void accept(ImmutableBuffer frameBuffer) {
        if (frameBuffer.getByte(FRAME_OFFSET_TYPE) != TYPE_REQ || !validator.validate(frameBuffer)) {
            log.warn("Callback Frame validation failed: {}", BuffersUtil.asString(frameBuffer));
        } else if (log.isDebugEnabled()) {
            log.debug("Callback Frame received: {}", BuffersUtil.asString(frameBuffer));
        }

        try {
            bufferInterceptors.forEach(interceptor -> interceptor.intercept(frameBuffer));
            ZWaveCallback callback = parser.parseCallbackFrame(frameBuffer);
            callbackInterceptors.forEach(interceptor -> interceptor.intercept(callback));
        } catch (FrameParseException e) {
            log.warn("Callback Frame parse failed: {}", BuffersUtil.asString(frameBuffer));
            log.debug(e.getMessage(), e);
        }
    }

    public InterceptableCallbackHandler addCallbackInterceptor(CallbackInterceptor interceptor) {
        callbackInterceptors.add(interceptor);
        return this;
    }

    public InterceptableCallbackHandler addFrameBufferInterceptor(FrameBufferInterceptor interceptor) {
        bufferInterceptors.add(interceptor);
        return this;
    }
}
