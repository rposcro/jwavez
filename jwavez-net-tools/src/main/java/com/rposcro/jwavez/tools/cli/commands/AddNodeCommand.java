package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialChannelManager;
import com.rposcro.jwavez.serial.transactions.AddNodeToNetworkTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import com.rposcro.jwavez.serial.transactions.TransactionStatus;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.AddNodeOptions;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;

public class AddNodeCommand implements Command {

  private AddNodeOptions options;
  private SerialChannel serialChannel;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new AddNodeOptions(args);
  }

  @Override
  public void execute(CommandLine commandLine) {
    serialChannel = SerialChannelManager.builder()
        .device(options.getDevice())
        .manageThreads(true)
        .build()
        .connect();
    System.out.println("Starting node inclusion transaction ...");
    Future<TransactionResult<NodeInfo>> futureResult = launchTransaction();
    System.out.println("Awaiting for new nodes ...");
    processResult(futureResult);
    System.out.println("End of inclusion transaction");
  }

  private Future<TransactionResult<NodeInfo>> launchTransaction() {
    AddNodeToNetworkTransaction transaction = new AddNodeToNetworkTransaction();
    return serialChannel.executeTransaction(transaction, options.getTimeout());
  }

  private void processResult(Future<TransactionResult<NodeInfo>> futureResult) {
    try {
      TransactionResult<NodeInfo> result = futureResult.get();
      if (result.getStatus() == TransactionStatus.Completed) {
        System.out.println("Inclusion succeeded, new node found");
        processNewNodeInfo(result.getResult());
      } else if (result.getStatus() == TransactionStatus.Cancelled) {
        System.out.println("Inclusion stopped by timeout");
      } else {
        System.out.println("Inclusion failed by unknown reason");
      }
    } catch(Exception e) {
      System.out.println("Inclusion transaction interrupted by an error: " + e.getMessage());
    }
  }

  private void processNewNodeInfo(NodeInfo nodeInfo) {
    if (nodeInfo == null) {
      System.out.println("Note! Included node information unavailable");
    } else {
      StringBuffer logMessage = new StringBuffer();
      List<String> commandClasses = Arrays.stream(nodeInfo.getCommandClasses())
          .map(clazz -> clazz.toString())
          .collect(Collectors.toList());
      logMessage.append("New node info:\n")
          .append(String.format("  node id: %s\n", nodeInfo.getId()))
          .append(String.format("  basic device class: %s\n", nodeInfo.getBasicDeviceClass()))
          .append(String.format("  generic device class: %s\n", nodeInfo.getGenericDeviceClass()))
          .append(String.format("  specific device class: %s\n", nodeInfo.getSpecificDeviceClass()))
          .append(String.format("  command classes: %s\n", String.join(", ", commandClasses)));
      System.out.println(logMessage);
    }
  }
}
