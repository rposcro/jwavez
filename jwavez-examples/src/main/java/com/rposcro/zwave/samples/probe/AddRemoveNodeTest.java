package com.rposcro.zwave.samples.probe;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.probe.transactions.AddNodeToNetworkTransaction;
import com.rposcro.jwavez.serial.probe.transactions.RemoveNodeFromNetworkTransaction;
import com.rposcro.jwavez.serial.probe.transactions.TransactionResult;
import com.rposcro.jwavez.serial.probe.transactions.TransactionStatus;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddRemoveNodeTest  extends AbstractExample {

  public AddRemoveNodeTest() {
    super("/dev/cu.usbmodem1421");
  }

  private void printResult(TransactionResult<NodeInfo> result) {
    StringBuffer logMessage = new StringBuffer(String.format("Transaction status: %s", result.getStatus()));
    if (result.getStatus() == TransactionStatus.Completed) {
      NodeInfo nodeInfo = result.getResult();
      List<String> commandClasses = Arrays.stream(nodeInfo.getCommandClasses())
          .map(clazz -> clazz.toString())
          .collect(Collectors.toList());
      logMessage.append("NodeInfo:\n")
          .append(String.format("  node id: %s\n", nodeInfo.getId()))
          .append(String.format("  basic device class: %s\n", nodeInfo.getBasicDeviceClass()))
          .append(String.format("  generic device class: %s\n", nodeInfo.getGenericDeviceClass()))
          .append(String.format("  specific device class: %s\n", nodeInfo.getSpecificDeviceClass()))
          .append(String.format("  command classes: %s\n", String.join(", ", commandClasses)))
      ;
    }

    System.out.println(logMessage.toString());
  }

  /**
   * Activates node inclusion.
   */
  private void runAddNode() throws Exception {
    AddNodeToNetworkTransaction transaction = new AddNodeToNetworkTransaction();
    TransactionResult<NodeInfo> result = channel.executeTransaction(transaction).get();
    printResult(result);
  }

  /**
   * Activates node exclusion.
   */
  private void runRemoveNode() throws Exception {
    RemoveNodeFromNetworkTransaction transaction = new RemoveNodeFromNetworkTransaction();
    TransactionResult<NodeInfo> result = channel.executeTransaction(transaction).get();
    printResult(result);
  }

  public static void main(String[] args) throws Exception {
    AddRemoveNodeTest test = new AddRemoveNodeTest();
    test.runAddNode();
    //test.runRemoveNode();

    Thread.sleep(5 * 60 * 1000);
    System.exit(0);
  }
}
