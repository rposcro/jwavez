package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialChannelManager;
import com.rposcro.jwavez.tools.cli.options.AbstractDeviceBasedOptions;

public abstract class AbstractDeviceCommand implements Command {

  protected SerialChannel serialChannel;

  public void connect(AbstractDeviceBasedOptions options) {
    this.serialChannel = SerialChannelManager.builder()
        .device(options.getDevice())
        .manageThreads(true)
        .build()
        .connect();
  }
}
