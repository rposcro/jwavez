package com.rposcro.jwavez.serial.handlers;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.interceptors.ViewBufferInterceptor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ViewBufferHandler implements Consumer<ViewBuffer> {

  private List<ViewBufferInterceptor> interceptors;

  public ViewBufferHandler() {
    this.interceptors = new ArrayList<>();
  }

  @Override
  public void accept(ViewBuffer viewBuffer) {
    interceptors.forEach(interceptor -> interceptor.intercept(viewBuffer));
  }

  public ViewBufferHandler addInterceptor(ViewBufferInterceptor interceptor) {
    interceptors.add(interceptor);
    return this;
  }
}
