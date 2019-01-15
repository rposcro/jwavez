package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialChannelManager;
import com.rposcro.jwavez.serial.transactions.RemoveNodeFromNetworkTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import com.rposcro.jwavez.serial.transactions.TransactionStatus;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.DefaultNodeBasedOptions;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;

public class ExcludeNodeCommand implements Command {

  private DefaultNodeBasedOptions options;
  private SerialChannel serialChannel;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new DefaultNodeBasedOptions(args);
  }

  @Override
  public void execute(CommandLine commandLine) {
    serialChannel = SerialChannelManager.builder()
        .device(options.getDevice())
        .manageThreads(true)
        .build()
        .connect();
    System.out.println("Starting node exclusion transaction ...");
    Future<TransactionResult<NodeInfo>> futureResult = launchTransaction();
    System.out.println("Awaiting for node to remove ...");
    processResult(futureResult);
    System.out.println("End of exclusion transaction");
  }

  private Future<TransactionResult<NodeInfo>> launchTransaction() {
    RemoveNodeFromNetworkTransaction transaction = new RemoveNodeFromNetworkTransaction();
    return serialChannel.executeTransaction(transaction, options.getTimeout());
  }

  private void processResult(Future<TransactionResult<NodeInfo>> futureResult) {
    try {
      TransactionResult<NodeInfo> result = futureResult.get();
      if (result.getStatus() == TransactionStatus.Completed) {
        System.out.println("Exclusion succeeded, node removed");
        processNodeInfo(result.getResult());
      } else if (result.getStatus() == TransactionStatus.Cancelled) {
        System.out.println("Exclusion stopped by timeout");
      } else {
        System.out.println("Exclusion failed by unknown reason");
      }
    } catch(Exception e) {
      System.out.println("Exclusion transaction interrupted by an error: " + e.getMessage());
    }
  }

  private void processNodeInfo(NodeInfo nodeInfo) {
    if (nodeInfo == null) {
      System.out.println("Note! Excluded node information unavailable");
    } else {
      StringBuffer logMessage = new StringBuffer();
      List<String> commandClasses = Arrays.stream(nodeInfo.getCommandClasses())
          .map(clazz -> clazz.toString())
          .collect(Collectors.toList());
      logMessage.append("Removed node info:\n")
          .append(String.format("  node id: %s\n", nodeInfo.getId()))
          .append(String.format("  basic device class: %s\n", nodeInfo.getBasicDeviceClass()))
          .append(String.format("  generic device class: %s\n", nodeInfo.getGenericDeviceClass()))
          .append(String.format("  specific device class: %s\n", nodeInfo.getSpecificDeviceClass()))
          .append(String.format("  command classes: %s\n", String.join(", ", commandClasses)));
      System.out.println(logMessage);
    }
  }
}
