package com.rposcro.jwavez.serial.handlers;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.interceptors.ViewBufferInterceptor;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.serial.rxtx.ResponseHandler;

import java.util.ArrayList;
import java.util.List;

public class InterceptableViewBufferHandler implements CallbackHandler, ResponseHandler {

    private List<ViewBufferInterceptor> interceptors;

    public InterceptableViewBufferHandler() {
        this.interceptors = new ArrayList<>();
    }

    @Override
    public void accept(ViewBuffer viewBuffer) {
        interceptors.forEach(interceptor -> interceptor.intercept(viewBuffer));
    }

    public InterceptableViewBufferHandler addInterceptor(ViewBufferInterceptor interceptor) {
        interceptors.add(interceptor);
        return this;
    }
}
