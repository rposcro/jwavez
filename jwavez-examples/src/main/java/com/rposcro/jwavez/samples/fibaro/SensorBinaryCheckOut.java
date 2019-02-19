package com.rposcro.jwavez.samples.fibaro;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.controlled.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.enums.AssociationCommandType;
import com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.association.AssociationGroupingsReport;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.samples.AbstractExample;
import com.rposcro.jwavez.serial.controllers.SimpleResponseController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.probe.interceptors.ApplicationCommandDispatcher;
import com.rposcro.jwavez.serial.probe.interceptors.ApplicationCommandHandlerLogger;
import com.rposcro.jwavez.serial.probe.interceptors.ApplicationUpdateLogger;
import com.rposcro.jwavez.serial.probe.transactions.SendDataTransaction;
import com.rposcro.jwavez.serial.probe.transactions.TransactionResult;
import com.rposcro.zwave.samples.probe.AbstractExample;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SensorBinaryCheckOut extends AbstractExample {

  private final NodeId nodeId;
  private final SupportedCommandDispatcher commandDspatcher;

  public SensorBinaryCheckOut(int nodeId, String device) {
    super(device, new ApplicationUpdateLogger(), new ApplicationCommandHandlerLogger());
    this.commandDspatcher = new SupportedCommandDispatcher();
    this.nodeId = new NodeId((byte) nodeId);

    ApplicationCommandDispatcher dispatcherInterceptor = ApplicationCommandDispatcher.builder()
        .supportedCommandParser(SupportedCommandParser.defaultParser())
        .supportedCommandDispatcher(commandDspatcher)
        .build();

    this.commandDspatcher.registerHandler(AssociationCommandType.ASSOCIATION_REPORT, this::handleAssociationReport);
    this.commandDspatcher.registerHandler(AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT, this::handleAssociationGroupingsReport);
    this.commandDspatcher.registerHandler(ConfigurationCommandType.CONFIGURATION_REPORT, this::handleConfigurationReport);
    this.manager.addInboundFrameInterceptor(dispatcherInterceptor);
  }

  private void handleAssociationReport(ZWaveSupportedCommand command) {
    AssociationReport report = (AssociationReport) command;
    StringBuffer logMessage = new StringBuffer("\n")
        .append(String.format("  association group: %s\n", report.getGroupId()))
        .append(String.format("  max nodes supported: %s\n", report.getMaxNodesCountSupported()))
        .append(String.format("  present nodes count: %s\n", report.getNodesCount()))
        .append(String.format("  present nodes: %s\n", Arrays.stream(report.getNodeIds())
          .map(nodeId -> String.format("%02X", nodeId.getId()))
            .collect(Collectors.joining(","))));
    log.info(logMessage.toString());
  }

  private void handleAssociationGroupingsReport(ZWaveSupportedCommand command) {
    AssociationGroupingsReport report = (AssociationGroupingsReport) command;
    log.info("\n  supported association groups count {}\n", report.getGroupsCount());
  }

  private void handleConfigurationReport(ZWaveSupportedCommand command) {
    ConfigurationReport report = (ConfigurationReport) command;
    log.info("\n parameter {} value {}", report.getParameterNumber(), report.getValue());
  }

  private void printResult(String message, TransactionResult<Void> result) {
    StringBuffer logMessage = new StringBuffer(String.format("%s. Status: %s", message, result.getStatus()));
    log.debug(logMessage.toString());
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
    log.debug("Checking configuration");
    ConfigurationCommandBuilder commandBuilder = new ConfigurationCommandBuilder();
    for (int paramNumber = 1; paramNumber <= 14; paramNumber++) {
      send("Send get parameter " + paramNumber, new SendDataTransaction(nodeId, commandBuilder.buildGetParameterCommand(paramNumber)));
    }
  }

  private void runCheckout(int nodeId, String device) throws SerialException {
    try (SimpleResponseController controller = SimpleResponseController.builder()
        .device(device)
        .build()
        .connect();) {
      checkDongleIds(controller);
      checkNodesIds(controller);
    }
  }

  public static void main(String[] args) throws Exception {
    SensorBinaryCheckOut setup = new SensorBinaryCheckOut(3, "/dev/cu.usbmodem1411");
    setup.learnAssociations();
    setup.learnConfiguration();


    Thread.sleep(60_000);
    System.exit(0);
  }
}
