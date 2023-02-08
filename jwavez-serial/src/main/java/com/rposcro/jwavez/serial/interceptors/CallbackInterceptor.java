package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;

@FunctionalInterface
public interface CallbackInterceptor {

    void intercept(ZWaveCallback callback);
}
