package com.rposcro.zstick;

import com.rposcro.jwavez.commands.controlled.ConfigurationControlledCommand;
import com.rposcro.jwavez.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.commands.controlled.MultiChannelControlledCommand;
import com.rposcro.jwavez.commands.controlled.SensorBinaryControlledCommand;
import com.rposcro.jwavez.model.NodeId;
import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;
import com.rposcro.jwavez.serial.debug.ApplicationCommandHandlerCatcher;
import com.rposcro.jwavez.serial.debug.ApplicationUpdateCatcher;
import com.rposcro.jwavez.serial.transactions.SendDataTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendDataTest {

  private SerialManager manager;
  private SerialChannel channel;

  public SendDataTest() {
    this.manager = new SerialManager("/dev/cu.usbmodem1411");
    this.channel = manager.connect();
    this.channel.addInboundFrameInterceptor(new ApplicationUpdateCatcher());
    this.channel.addInboundFrameInterceptor(new ApplicationCommandHandlerCatcher());
  }

  private void printResult(TransactionResult<Void> result) {
    StringBuffer logMessage = new StringBuffer(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(logMessage.toString());
  }

  private void runSendSensorBinaryGet() throws Exception {
    SendDataTransaction transaction = new SendDataTransaction(new NodeId((byte) 4), SensorBinaryControlledCommand.buildGetCommand());
    TransactionResult<Void> result = channel.executeTransaction(transaction).get();
    printResult(result);
  }

  private void runSendSensorBinaryGetSupported() throws Exception {
    SendDataTransaction transaction = new SendDataTransaction(new NodeId((byte) 4), SensorBinaryControlledCommand.buildGetSupportedSensorCommand());
    TransactionResult<Void> result = channel.executeTransaction(transaction).get();
    printResult(result);
  }

  private void runSend(int nodeId, Supplier<ZWaveControlledCommand> commandSupplier) throws Exception {
    Thread.sleep(500);
    SendDataTransaction transaction = new SendDataTransaction(
        new NodeId((byte) nodeId), commandSupplier.get());
    TransactionResult<Void> result = channel.executeTransaction(transaction).get();
    printResult(result);
  }

  private static void testSensorClass() throws Exception {
    SendDataTest test = new SendDataTest();
    test.runSend(4, SensorBinaryControlledCommand::buildGetCommand);
    test.runSend(4, MultiChannelControlledCommand::buildGet);
    test.runSend(4, MultiChannelControlledCommand::buildEndPointGetCommand);
    //test.runSend(4, MultiChannelControlledCommand::buildCapabilityGet);
    //test.runSend(4, MultiChannelControlledCommand::buildAggregatedMembersGet);
    //test.runSend(4, MultiChannelControlledCommand::buildEndPointFind);
  }

  private static void testConfigurationClass() throws Exception {
    SendDataTest test = new SendDataTest();
    test.runSend(4, () -> ConfigurationControlledCommand.buildGetParameterCommand(3));
    test.runSend(4, () -> ConfigurationControlledCommand.buildBulkGetParameterCommand(0, 14));
  }

  public static void main(String[] args) throws Exception {
    testConfigurationClass();

    Thread.sleep(60_000);
    System.exit(0);
  }
}
