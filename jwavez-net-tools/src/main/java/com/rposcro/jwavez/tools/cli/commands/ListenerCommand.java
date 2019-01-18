package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptorContext;
import com.rposcro.jwavez.serial.utils.FrameUtil;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.DefaultDeviceBasedOptions;

public class ListenerCommand extends AbstractDeviceCommand {

  private DefaultDeviceBasedOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new DefaultDeviceBasedOptions(args);
  }

  @Override
  public void execute() {
    connect(options);
    channelManager.addInboundFrameInterceptor(this::intercept);
    System.out.println("Listening to inbound frames, exit with Ctrl+C");
    while(true);
  }

  private void intercept(InboundFrameInterceptorContext context) {
    context.stopProcessing();
    SOFFrame frame = context.getFrame();
    System.out.printf("%s : %s : %s\n", frame.getFrameType(), frame.getSerialCommand(), FrameUtil.bufferToString(frame.getBuffer()));
  }
}
