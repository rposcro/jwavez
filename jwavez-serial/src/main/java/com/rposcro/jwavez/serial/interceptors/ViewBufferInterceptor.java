package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

@FunctionalInterface
public interface ViewBufferInterceptor {

    void intercept(ImmutableBuffer viewBuffer);
}
