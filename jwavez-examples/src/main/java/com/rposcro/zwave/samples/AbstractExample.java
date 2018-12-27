package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.factory.SerialChannelManager;

public abstract class AbstractExample {

  protected SerialChannelManager manager;
  protected SerialChannel channel;

  protected AbstractExample(String device) {
    this.manager = SerialChannelManager.builder()
        .device(device)
        .manageThreads(true)
        .build();
    this.channel = manager.connect();
  }
}
