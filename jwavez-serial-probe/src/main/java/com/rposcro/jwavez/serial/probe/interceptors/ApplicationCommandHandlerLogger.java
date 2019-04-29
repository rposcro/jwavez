package com.rposcro.jwavez.serial.probe.interceptors;

import com.rposcro.jwavez.serial.probe.frame.SOFFrame;
import com.rposcro.jwavez.serial.probe.frame.callbacks.ApplicationCommandHandlerCallbackFrame;
import com.rposcro.jwavez.serial.probe.rxtx.InboundFrameInterceptor;
import com.rposcro.jwavez.serial.probe.rxtx.InboundFrameInterceptorContext;
import com.rposcro.jwavez.serial.probe.utils.FrameUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationCommandHandlerLogger implements InboundFrameInterceptor {

  @Override
  public void intercept(InboundFrameInterceptorContext context) {
    SOFFrame frame = context.getFrame();
    if (frame instanceof ApplicationCommandHandlerCallbackFrame) {
      ApplicationCommandHandlerCallbackFrame commandFrame = (ApplicationCommandHandlerCallbackFrame) frame;
      StringBuffer logMessage = new StringBuffer("Application command handler:")
          .append(String.format("  source node id: %s\n", commandFrame.getSourceNodeId().getId()))
          .append(String.format("  command payloadLength: %s\n", commandFrame.getCommandLength()))
          .append(String.format("  command payload: %s\n", FrameUtil.bufferToString(commandFrame.getCommandPayload())))
          ;
      log.info(logMessage.toString());
    }
  }
}
