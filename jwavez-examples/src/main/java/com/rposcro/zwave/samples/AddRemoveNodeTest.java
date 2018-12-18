package com.rposcro.zwave.samples;

import com.rposcro.jwavez.model.NodeInfo;
import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;
import com.rposcro.jwavez.serial.transactions.AddNodeToNetworkTransaction;
import com.rposcro.jwavez.serial.transactions.RemoveNodeFromNetworkTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import com.rposcro.jwavez.serial.transactions.TransactionStatus;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddRemoveNodeTest {

  private SerialManager manager;
  private SerialChannel channel;

  public AddRemoveNodeTest() {
    this.manager = new SerialManager("/dev/cu.usbmodem1411");
    this.channel = manager.connect();
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

  private void runAddNode() throws Exception {
    AddNodeToNetworkTransaction transaction = new AddNodeToNetworkTransaction();
    TransactionResult<NodeInfo> result = channel.executeTransaction(transaction).get();
    printResult(result);
  }

  private void runRemoveNode() throws Exception {
    RemoveNodeFromNetworkTransaction transaction = new RemoveNodeFromNetworkTransaction();
    TransactionResult<NodeInfo> result = channel.executeTransaction(transaction).get();
    printResult(result);
  }

  private static void testIntegers() {
    long numMax = Long.MAX_VALUE;
    long numMaxP1 = numMax + 1;
    System.out.println(String.format("max: %s, maxP1: %s", numMax, numMaxP1));
    System.out.println(String.format("max ? maxP1 = %s", Long.compare(numMax, numMaxP1)));
    System.out.println(String.format("max unsigned? maxP1 = %s", Long.compareUnsigned(numMax, numMaxP1)));

    long num1 = Long.MAX_VALUE + 1;
    long num2 = num1 + 1;
    System.out.println(String.format("num1: %s, num2: %s", num1, num2));
    System.out.println(String.format("num1 ? num2 = %s", Long.compare(num1, num2)));
    System.out.println(String.format("num1 unsigned? num2 = %s", Long.compareUnsigned(num1, num2)));
  }

  public static void main(String[] args) throws Exception {
    AddRemoveNodeTest test = new AddRemoveNodeTest();
    test.runAddNode();
    //test.runRemoveNode();

    Thread.sleep(3600000);
    //System.exit(0);
  }

  private static String orNull(byte[] array) {
    return array == null ? "null" : "" + array[0];
  }
}
