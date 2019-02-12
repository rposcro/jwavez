package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.serial.frames.callbacks.Callback;

@FunctionalInterface
public interface CallbackInterceptor {

  void intercept(Callback callback);
}
