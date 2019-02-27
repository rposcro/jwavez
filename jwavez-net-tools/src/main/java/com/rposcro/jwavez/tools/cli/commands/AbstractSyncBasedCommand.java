package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.options.AbstractDeviceBasedOptions;

public abstract class AbstractSyncBasedCommand extends AbstractCommand {

  protected BasicSynchronousController controller;

  protected void connect(AbstractDeviceBasedOptions options) throws CommandExecutionException {
    try {
      controller = BasicSynchronousController.builder()
          .device(options.getDevice())
          .build()
          .connect();
    } catch(SerialPortException e) {
      throw new CommandExecutionException("Failed to open serial port", e);
    }
  }

  @Override
  public void close() throws SerialPortException {
    controller.close();
  }
}
