package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialChannelManager;
import com.rposcro.jwavez.tools.cli.options.AbstractDeviceTimeoutBasedOptions;

public abstract class AbstractDeviceCommand implements Command {

  protected SerialChannelManager channelManager;
  protected SerialChannel serialChannel;

  public void connect(AbstractDeviceTimeoutBasedOptions options) {
    this.channelManager = SerialChannelManager.builder()
        .device(options.getDevice())
        .manageThreads(true)
        .build();
    this.serialChannel = channelManager.connect();
  }
}
