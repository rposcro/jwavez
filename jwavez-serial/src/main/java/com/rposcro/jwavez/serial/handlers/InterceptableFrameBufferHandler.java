package com.rposcro.jwavez.serial.handlers;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.interceptors.FrameBufferInterceptor;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.serial.rxtx.ResponseHandler;

import java.util.ArrayList;
import java.util.List;

public class InterceptableFrameBufferHandler implements CallbackHandler, ResponseHandler {

    private List<FrameBufferInterceptor> interceptors;

    public InterceptableFrameBufferHandler() {
        this.interceptors = new ArrayList<>();
    }

    @Override
    public void accept(ImmutableBuffer frameBuffer) {
        interceptors.forEach(interceptor -> interceptor.intercept(frameBuffer));
    }

    public InterceptableFrameBufferHandler addInterceptor(FrameBufferInterceptor interceptor) {
        interceptors.add(interceptor);
        return this;
    }
}
