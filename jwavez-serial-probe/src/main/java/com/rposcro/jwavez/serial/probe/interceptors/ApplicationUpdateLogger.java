package com.rposcro.jwavez.serial.probe.interceptors;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.probe.frame.SOFFrame;
import com.rposcro.jwavez.serial.probe.frame.callbacks.ApplicationUpdateCallbackFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.ApplicationUpdateStatus;
import com.rposcro.jwavez.serial.probe.rxtx.InboundFrameInterceptor;
import com.rposcro.jwavez.serial.probe.rxtx.InboundFrameInterceptorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationUpdateLogger implements InboundFrameInterceptor {

  @Override
  public void intercept(InboundFrameInterceptorContext context) {
    SOFFrame frame = context.getFrame();
    if (frame instanceof ApplicationUpdateCallbackFrame) {
      ApplicationUpdateCallbackFrame updateFrame = (ApplicationUpdateCallbackFrame) frame;
      log.info("Node info received with status: {}", updateFrame.getStatus());
      if (updateFrame.getStatus() == ApplicationUpdateStatus.APP_UPDATE_STATUS_NODE_INFO_RECEIVED) {
        NodeInfo nodeInfo = updateFrame.getNodeInfo();
        List<String> commandClasses = Arrays.stream(nodeInfo.getCommandClasses())
            .map(clazz -> clazz.toString())
            .collect(Collectors.toList());
        StringBuffer logMessage = new StringBuffer()
            .append(String.format("node id: %s\n", nodeInfo.getId()))
            .append(String.format("basic dongleDevice class: %s\n", nodeInfo.getBasicDeviceClass()))
            .append(String.format("generic dongleDevice class: %s\n", nodeInfo.getGenericDeviceClass()))
            .append(String.format("specific dongleDevice class: %s\n", nodeInfo.getSpecificDeviceClass()))
            .append(String.format("command classes: %s\n", String.join(", ", commandClasses)))
                ;
        log.info(logMessage.toString());
      }
    }
  }
}
