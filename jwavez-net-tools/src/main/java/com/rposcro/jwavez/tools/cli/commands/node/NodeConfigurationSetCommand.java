package com.rposcro.jwavez.tools.cli.commands.node;

import static com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType.CONFIGURATION_REPORT;

import com.rposcro.jwavez.core.commands.controlled.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.serial.transactions.SendDataTransaction;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeConfigurationReadOptions;
import com.rposcro.jwavez.tools.cli.options.node.NodeConfigurationSetOptions;
import java.util.stream.Stream;

public class NodeConfigurationSetCommand extends AbstractNodeCommand {

  private NodeConfigurationSetOptions options;
  private ConfigurationCommandBuilder commandBuilder;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NodeConfigurationSetOptions(args);
    commandBuilder = new ConfigurationCommandBuilder();
  }

  @Override
  public void execute() throws CommandExecutionException {
    connect(options);
    System.out.println("Setting configuration parameter value...");
    setConfiguration();
    printReport(readConfiguration(options.getParameterNumber()));
  }

  private void setConfiguration() throws CommandExecutionException {

    ZWaveControlledCommand zWaveCommand = commandBuilder.buildSetParameterCommand(options.getParameterNumber(), options.getParameterValue(), options.getParameterSize());
    SendDataTransaction transaction = new SendDataTransaction(options.getNodeId(), zWaveCommand, false);
    executeTransaction(transaction, options.getTimeout(DEFAULT_CALLBACK_TIMEOUT));
  }

  private void printReport(ConfigurationReport report) {
    System.out.println();
    System.out.printf(":: Report on configuration parameter %s\n", report.getParameterNumber());
    System.out.printf("  value size: %s bits\n", (8 * report.getValueSize()));
    System.out.printf("  value: %s\n", formatValue(report.getValueSize(), report.getValue()));
    System.out.println();
  }

  private ConfigurationReport readConfiguration(int parameterNumber) throws CommandExecutionException {
    System.out.printf("Reading configuration parameter %s...\n", parameterNumber);
    SendDataTransaction transaction = new SendDataTransaction(options.getNodeId(), commandBuilder.buildGetParameterCommand(parameterNumber), false);
    ConfigurationReport report = (ConfigurationReport) requestZWaveCommand(transaction, CONFIGURATION_REPORT, options.getTimeout(DEFAULT_CALLBACK_TIMEOUT));
    return report;
  }

  private String formatValue(int size, int value) {
    switch(size) {
      case 1:
        return String.format("%02X", (byte) value);
      case 2:
        return String.format("%04X", (short) value);
      default:
        return String.format("%08X", value);
    }
  }
}
