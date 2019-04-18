package com.rposcro.jwavez.tools.cli.commands.node;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationUpdateCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.requests.RequestNodeInfoRequest;
import com.rposcro.jwavez.serial.frames.responses.RequestNodeInfoResponse;
import com.rposcro.jwavez.serial.model.ApplicationUpdateStatus;
import com.rposcro.jwavez.tools.cli.ZWaveCLI;
import com.rposcro.jwavez.tools.cli.commands.AbstractAsyncBasedCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.DefaultNodeBasedOptions;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NodeInfoCommand extends AbstractAsyncBasedCommand {

  private DefaultNodeBasedOptions options;
  private Semaphore doneLock;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new DefaultNodeBasedOptions(args);
  }

  @Override
  public void execute() throws CommandExecutionException {
    connect(options).addCallbackInterceptor(this::handleUpdate);

    try {
      RequestNodeInfoResponse response = controller.requestResponseFlow(
          RequestNodeInfoRequest.createRequestNodeInfoRequest(options.getNodeId()));
      if (response.isRequestAccepted()) {
        doneLock = new Semaphore(1);
        doneLock.acquireUninterruptibly(1);
        if (!doneLock.tryAcquire(1, options.getTimeout(), TimeUnit.MILLISECONDS)) {
          System.out.println("Node information request failed due to timeout");
        }
      } else {
        System.out.println("Request was not accepted by dongle");
      }
    } catch(Exception e) {
      log.debug("Command failed with error", e);
      System.out.printf("Node information request failed due to an error: %s\n", e.getMessage());
    }

    System.out.println("End of transaction");
  }

  private void handleUpdate(ZWaveCallback callback) {
    if (callback instanceof ApplicationUpdateCallback) {
      ApplicationUpdateCallback updateCallback = (ApplicationUpdateCallback) callback;
      if (updateCallback.getStatus() == ApplicationUpdateStatus.APP_UPDATE_STATUS_NODE_INFO_RECEIVED) {
        NodeInfo nodeInfo = updateCallback.getNodeInfo();
        List<String> commandClasses = Arrays.stream(nodeInfo.getCommandClasses())
            .map(clazz -> clazz.toString())
            .collect(Collectors.toList());
        StringBuffer logMessage = new StringBuffer("Node info successfully received\n")
            .append(String.format("  node id: %02X\n", nodeInfo.getId().getId()))
            .append(String.format("  basic dongleDevice class: %s\n", nodeInfo.getBasicDeviceClass()))
            .append(String.format("  generic dongleDevice class: %s\n", nodeInfo.getGenericDeviceClass()))
            .append(String.format("  specific dongleDevice class: %s\n", nodeInfo.getSpecificDeviceClass()))
            .append(String.format("  command classes: %s\n", String.join(", ", commandClasses)));
        System.out.println(logMessage.toString());
        doneLock.release();
      } else {
        System.out.println("Unexpected command update status: " + updateCallback.getStatus());
      }
    } else {
      log.debug("Skipping callback: {}", callback.getSerialCommand());
    }
  }

  public static void main(String... args) throws Exception {
    ZWaveCLI.main("node", "class", "-d", "/dev/tty.usbmodem1421", "-n", "3");
  }
}
