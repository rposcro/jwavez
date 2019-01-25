package com.rposcro.zwave.samples;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommandBuilder;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.interceptors.ApplicationCommandHandlerLogger;
import com.rposcro.jwavez.serial.probe.interceptors.ApplicationUpdateLogger;
import com.rposcro.jwavez.serial.probe.transactions.SendDataTransaction;
import com.rposcro.jwavez.serial.probe.transactions.TransactionResult;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendDataTest extends AbstractExample {

  private ZWaveControlledCommandBuilder commandBuilder;

  public SendDataTest() {
    super("/dev/cu.usbmodem1411", new ApplicationUpdateLogger(), new ApplicationCommandHandlerLogger());
    this.commandBuilder = new ZWaveControlledCommandBuilder();
  }

  private void printResult(TransactionResult<Void> result) {
    log.info("Transaction status: {}", result.getStatus());
  }

  private void runSend(NodeId nodeId, Supplier<ZWaveControlledCommand> commandSupplier) throws Exception {
    Thread.sleep(500); // Wait to avoid serial line race
    SendDataTransaction transaction = new SendDataTransaction(nodeId, commandSupplier.get());
    TransactionResult<Void> result = channel.executeTransaction(transaction).get();
    printResult(result);
  }

  /**
   * Checks binary node's input values using Binary Sensor Command Class
   * @param nodeId
   */
  private void testSensorClass(NodeId nodeId) throws Exception {
    runSend(nodeId, commandBuilder.sensorBinaryControlledCommand()::buildGetCommand);
    runSend(nodeId, commandBuilder.multiChannelCommandBuilder()::buildGet);
    runSend(nodeId, commandBuilder.multiChannelCommandBuilder()::buildEndPointGetCommand);

    // Not all slave nodes accept these commands
    //runSend(nodeId, commandBuilder.multiChannelCommandBuilder()::buildCapabilityGet);
    //runSend(nodeId, commandBuilder.multiChannelCommandBuilder()::buildAggregatedMembersGet);
    //runSend(nodeId, commandBuilder.multiChannelCommandBuilder()::buildEndPointFind);
  }

  /**
   * Checks node's configuration parameter values using Configuration Command Class.
   * @param nodeId
   */
  private void testConfigurationClass(NodeId nodeId) throws Exception {
    runSend(nodeId, () -> commandBuilder.configurationCommandBuilder().buildGetParameterCommand(3));
    runSend(nodeId, () -> commandBuilder.configurationCommandBuilder().buildBulkGetParameterCommand(0, 14));
  }

  public static void main(String[] args) throws Exception {
    SendDataTest test = new SendDataTest();
    test.testConfigurationClass(new NodeId(4));
    Thread.sleep(60_000);
    System.exit(0);
  }
}
