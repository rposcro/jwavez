package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;

@FunctionalInterface
public interface ViewBufferInterceptor {

    void intercept(ViewBuffer viewBuffer);
}
