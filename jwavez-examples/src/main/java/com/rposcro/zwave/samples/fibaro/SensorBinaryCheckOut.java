package com.rposcro.zwave.samples.fibaro;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.controlled.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandDispatcher;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandHandlerLogger;
import com.rposcro.jwavez.serial.interceptors.ApplicationUpdateCatcher;
import com.rposcro.jwavez.serial.transactions.SendDataTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import com.rposcro.zwave.samples.AbstractExample;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SensorBinaryCheckOut extends AbstractExample {

  private final NodeId nodeId;
  private final SupportedCommandDispatcher commandDspatcher;

  public SensorBinaryCheckOut(int nodeId, String device) {
    super(device, new ApplicationUpdateCatcher(), new ApplicationCommandHandlerLogger());
    this.commandDspatcher = new SupportedCommandDispatcher();
    this.nodeId = new NodeId((byte) nodeId);

    ApplicationCommandDispatcher dispatcherInterceptor = ApplicationCommandDispatcher.builder()
        .supportedCommandParser(SupportedCommandParser.defaultParser())
        .supportedCommandDispatcher(commandDspatcher)
        .build();

    this.commandDspatcher.registerHandler(ConfigurationCommandType.CONFIGURATION_GET, this::handleCheckCallback);
    this.manager.addInboundFrameInterceptor(dispatcherInterceptor);
  }

  private void handleCheckCallback(ZWaveSupportedCommand command) {
    ConfigurationReport report = (ConfigurationReport) command;
    String.format("Parameter {} value {}", report.getParameterNumber(), report.getValue());
  }

  private void printResult(String message, TransactionResult<Void> result) {
    StringBuffer logMessage = new StringBuffer(String.format("%s. Status: %s", message, result.getStatus()));
    System.out.println(logMessage.toString());
  }

  private void send(String message, SendDataTransaction transaction) throws Exception {
    Thread.sleep(500);
    printResult(message, channel.executeTransaction(transaction).get());
  }

  private void learnAssociations() throws Exception {
    AssociationCommandBuilder commandBuilder = new AssociationCommandBuilder();
    send("Get supported groupings", new SendDataTransaction(nodeId, commandBuilder.buildGetSupportedGroupingsCommand()));
    send("Get group 1", new SendDataTransaction(nodeId, commandBuilder.buildGetCommand(1)));
    send("Get group 2", new SendDataTransaction(nodeId, commandBuilder.buildGetCommand(2)));
    send("Get group 3", new SendDataTransaction(nodeId, commandBuilder.buildGetCommand(3)));
  }

  private void learnConfiguration() throws Exception {
    ConfigurationCommandBuilder commandBuilder = new ConfigurationCommandBuilder();
    for (int paramNumber = 1; paramNumber <= 14; paramNumber++) {
      send("Send get parameter " + paramNumber, new SendDataTransaction(nodeId, commandBuilder.buildGetParameterCommand(paramNumber)));
    }
  }

  public static void main(String[] args) throws Exception {
    SensorBinaryCheckOut setup = new SensorBinaryCheckOut(4, "/dev/cu.usbmodem1411");
    setup.learnAssociations();

    Thread.sleep(60_000);
    System.exit(0);
  }
}
