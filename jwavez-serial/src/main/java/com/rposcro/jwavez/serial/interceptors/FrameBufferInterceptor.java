package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

@FunctionalInterface
public interface FrameBufferInterceptor {

    void intercept(ImmutableBuffer frameBuffer);
}
