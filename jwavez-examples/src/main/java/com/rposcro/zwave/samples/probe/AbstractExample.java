package com.rposcro.zwave.samples.probe;

import com.rposcro.jwavez.serial.probe.SerialChannel;
import com.rposcro.jwavez.serial.probe.SerialChannelManager;
import com.rposcro.jwavez.serial.probe.rxtx.InboundFrameInterceptor;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
