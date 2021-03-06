package com.rposcro.jwavez.tools.cli.commands.network;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import com.rposcro.jwavez.serial.utils.FrameUtil;
import com.rposcro.jwavez.tools.cli.commands.AbstractAsyncBasedCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.DefaultDeviceBasedOptions;
import com.rposcro.jwavez.tools.cli.utils.ProcedureUtil;

public class ListenerCommand extends AbstractAsyncBasedCommand {

  private DefaultDeviceBasedOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new DefaultDeviceBasedOptions(args);
  }

  @Override
  public void execute() {
    System.out.println("Frame listener starting ...");
    ProcedureUtil.executeProcedure(this::startListening);
  }

  private void startListening() throws SerialException {
    connect(options).addCallbackInterceptor(this::intercept);
    System.out.println("Listening to inbound frames, exit with Ctrl+C");
    while(true);
  }

  private void intercept(ViewBuffer callback) {
    System.out.printf("%s : %s : %s\n",
        FrameUtil.type(callback),
        FrameUtil.serialCommand(callback),
        BufferUtil.bufferToString(callback));
  }
}
