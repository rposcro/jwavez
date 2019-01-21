package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialChannelManager;
import com.rposcro.jwavez.tools.cli.options.AbstractDeviceBasedOptions;

public abstract class AbstractDeviceCommand implements Command {

  protected SerialChannelManager channelManager;
  protected SerialChannel serialChannel;

  public void connect(AbstractDeviceBasedOptions options) {
    this.channelManager = SerialChannelManager.builder()
        .device(options.getDevice())
        .manageThreads(true)
        .build();
    this.serialChannel = channelManager.connect();
  }

  public void close() {
    channelManager.disconnect();
  }
}
