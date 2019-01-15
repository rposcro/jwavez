package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.callbacks.ApplicationUpdateCallbackFrame;
import com.rposcro.jwavez.serial.frame.constants.ApplicationUpdateStatus;
import com.rposcro.jwavez.serial.frame.requests.RequestNodeInfoRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.RequestNodeInfoResponseFrame;
import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptorContext;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import com.rposcro.jwavez.serial.transactions.TransactionStatus;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.DefaultNodeBasedOptions;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;

public class NodeAssociationRemoveCommand extends AbstractDeviceCommand {

  private DefaultNodeBasedOptions options;
  private Semaphore lock;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new DefaultNodeBasedOptions(args);
  }

  @Override
  public void execute(CommandLine commandLine) {
    connect(options);
    lock = new Semaphore(1);
    channelManager.addInboundFrameInterceptor(this::handleUpdate);
    System.out.println("Requesting node information transaction...");

    if (launchTransaction()) {
      try {
        lock.acquireUninterruptibly();
        if (!lock.tryAcquire(1, options.getTimeout(), TimeUnit.MILLISECONDS)) {
          System.out.println("Node information request failed due to timeout");
        }
        ;
      } catch (InterruptedException e) {
        System.out.println("Unexpected error occurred when awaiting callback");
      }
    }

    System.out.println("End of transaction");
  }

  private boolean launchTransaction() {
    TransactionResult<RequestNodeInfoResponseFrame> result = serialChannel.sendFrameWithResponseAndWait(
        new RequestNodeInfoRequestFrame(options.getNodeId()));
    if (result.getStatus() == TransactionStatus.Completed) {
      System.out.println("Request successfully sent, awaiting node info callback...");
      return true;
    } else {
      System.out.println("Failed to place node info request");
      return false;
    }
  }

  private void handleUpdate(InboundFrameInterceptorContext context) {
    SOFFrame frame = context.getFrame();
    if (frame instanceof ApplicationUpdateCallbackFrame) {
      ApplicationUpdateCallbackFrame updateCallback = (ApplicationUpdateCallbackFrame) frame;
      if (updateCallback.getStatus() == ApplicationUpdateStatus.APP_UPDATE_STATUS_NODE_INFO_RECEIVED) {
        NodeInfo nodeInfo = updateCallback.getNodeInfo();
        List<String> commandClasses = Arrays.stream(nodeInfo.getCommandClasses())
            .map(clazz -> clazz.toString())
            .collect(Collectors.toList());
        StringBuffer logMessage = new StringBuffer("Node info successfully received\n")
            .append(String.format("  node id: %02X\n", nodeInfo.getId().getId()))
            .append(String.format("  basic device class: %s\n", nodeInfo.getBasicDeviceClass()))
            .append(String.format("  generic device class: %s\n", nodeInfo.getGenericDeviceClass()))
            .append(String.format("  specific device class: %s\n", nodeInfo.getSpecificDeviceClass()))
            .append(String.format("  command classes: %s\n", String.join(", ", commandClasses)));
        System.out.println(logMessage.toString());
        lock.release();
      } else {
        System.out.println("Unexpected command update status: " + updateCallback.getStatus());
      }
    } else {
    }
  }
}
