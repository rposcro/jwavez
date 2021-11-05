package com.rposcro.jwavez.tools.cli.commands.network;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.cli.commands.Command;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.DefaultDeviceTimeoutBasedOptions;
import com.rposcro.jwavez.tools.utils.ProcedureUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcludeNodeCommand implements Command {

  protected DefaultDeviceTimeoutBasedOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    this.options = new DefaultDeviceTimeoutBasedOptions(args);
  }

  @Override
  public void execute() {
    System.out.println("Starting node exclusion transaction ...");
    ProcedureUtil.executeProcedure(this::runExclusion);
    System.out.println("Exclusion transaction finished");
  }

  private void runExclusion() throws SerialException {
    try(
        RemoveNodeFromNetworkController controller = RemoveNodeFromNetworkController.builder()
            .dongleDevice(options.getDevice())
            .build()
    ) {
      controller.connect();
      System.out.println("Awaiting for node to remove ...");
      Optional<NodeInfo> nodeInfo = controller.listenForNodeToRemove();
      processResult(nodeInfo);
    }
  }

  private void processResult(Optional<NodeInfo> nodeInfo) {
    if (nodeInfo.isPresent()) {
      System.out.println("Exclusion succeeded");
      processRemovedNodeInfo(nodeInfo.get());
    } else {
      System.out.println("Exclusion transaction didn't return node information, check dongle state");
    }
  }

  private void processRemovedNodeInfo(NodeInfo nodeInfo) {
    if (nodeInfo == null) {
      System.out.println("Note! Excluded node information unavailable");
    } else {
      StringBuffer logMessage = new StringBuffer();
      List<String> commandClasses = Arrays.stream(nodeInfo.getCommandClasses())
          .map(clazz -> clazz.toString())
          .collect(Collectors.toList());
      logMessage.append("Removed node info:\n")
          .append(String.format("  node id: %s\n", nodeInfo.getId()))
          .append(String.format("  basic dongleDevice class: %s\n", nodeInfo.getBasicDeviceClass()))
          .append(String.format("  generic dongleDevice class: %s\n", nodeInfo.getGenericDeviceClass()))
          .append(String.format("  specific dongleDevice class: %s\n", nodeInfo.getSpecificDeviceClass()))
          .append(String.format("  command classes: %s\n", String.join(", ", commandClasses)));
      System.out.println(logMessage);
    }
  }
}
