package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;

@FunctionalInterface
public interface ResponseInterceptor {

    void intercept(ZWaveResponse response);
}
