package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialChannelManager;
import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptor;
import java.util.Arrays;

public abstract class AbstractExample {

  protected SerialChannelManager manager;
  protected SerialChannel channel;

  protected AbstractExample(String device, InboundFrameInterceptor... interceptors) {
    this.manager = SerialChannelManager.builder()
        .device(device)
        .manageThreads(true)
        .interceptors(Arrays.asList(interceptors))
        .build();
    this.channel = manager.connect();
  }
}
