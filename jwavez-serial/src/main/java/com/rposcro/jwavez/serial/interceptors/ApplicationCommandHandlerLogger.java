package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.callbacks.ApplicationCommandHandlerCallbackFrame;
import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptor;
import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptorContext;
import com.rposcro.jwavez.serial.utils.FrameUtil;
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
