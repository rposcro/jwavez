package com.rposcro.jwavez.serial.handlers;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_RES;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.InboundFrameValidator;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import com.rposcro.jwavez.serial.interceptors.ResponseInterceptor;
import com.rposcro.jwavez.serial.interceptors.FrameBufferInterceptor;
import com.rposcro.jwavez.serial.rxtx.ResponseHandler;
import com.rposcro.jwavez.serial.utils.BufferUtil;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InterceptableResponseHandler implements ResponseHandler {

    private InboundFrameValidator validator;
    private InboundFrameParser parser;

    private List<ResponseInterceptor> responseInterceptors;
    private List<FrameBufferInterceptor> bufferInterceptors;

    public InterceptableResponseHandler() {
        this.validator = new InboundFrameValidator();
        this.parser = new InboundFrameParser();
        this.responseInterceptors = new ArrayList<>();
        this.bufferInterceptors = new ArrayList<>();
    }

    @Override
    public void accept(ImmutableBuffer frameBuffer) {
        if (frameBuffer.getByte(FRAME_OFFSET_TYPE) != TYPE_RES || !validator.validate(frameBuffer)) {
            log.warn("Response frame validation failed: {}", BufferUtil.bufferToString(frameBuffer));
        } else if (log.isDebugEnabled()) {
            log.debug("Response frame received: {}", BufferUtil.bufferToString(frameBuffer));
        }

        try {
            bufferInterceptors.forEach(interceptor -> interceptor.intercept(frameBuffer));
            ZWaveResponse response = parser.parseResponseFrame(frameBuffer);
            responseInterceptors.forEach(interceptor -> interceptor.intercept(response));
        } catch (FrameParseException e) {
            log.warn("Response frame parse failed: {}", BufferUtil.bufferToString(frameBuffer));
        }
    }

    public InterceptableResponseHandler addResponseInterceptor(ResponseInterceptor interceptor) {
        responseInterceptors.add(interceptor);
        return this;
    }

    public InterceptableResponseHandler addFrameBufferInterceptor(FrameBufferInterceptor interceptor) {
        bufferInterceptors.add(interceptor);
        return this;
    }
}
