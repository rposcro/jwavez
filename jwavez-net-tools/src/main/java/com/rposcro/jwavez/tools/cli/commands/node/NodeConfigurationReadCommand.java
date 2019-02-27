package com.rposcro.jwavez.tools.cli.commands.node;

import static com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType.CONFIGURATION_REPORT;

import com.rposcro.jwavez.core.commands.controlled.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.tools.cli.ZWaveCLI;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeConfigurationReadOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NodeConfigurationReadCommand extends SendDataBasedCommand {

  private NodeConfigurationReadOptions options;
  private ConfigurationCommandBuilder commandBuilder;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NodeConfigurationReadOptions(args);
    commandBuilder = new ConfigurationCommandBuilder();
  }

  @Override
  public void execute() throws CommandExecutionException {
    connect(options);
    System.out.println("Requesting configuration parameter(s) information...");
    List<ConfigurationReport> reports = collectReports();
    printReport(reports);
  }

  private void printReport(List<ConfigurationReport> reports) {
    System.out.println();
    reports.stream()
        .forEachOrdered(report -> {
          System.out.printf(":: Report on configuration parameter %s\n", report.getParameterNumber());
          System.out.printf("  value size: %s bits\n", (8 * report.getValueSize()));
          System.out.printf("  value: %s\n", formatValue(report.getValueSize(), report.getValue()));
          System.out.println();
        });
  }

  private List<ConfigurationReport> collectReports() {
    int[] parameterNumbers = options.getParameterNumbers();
    List<ConfigurationReport> reports = new ArrayList<>(parameterNumbers.length);
    for (int paramIdx = 0; paramIdx < parameterNumbers.length; paramIdx++) {
      try {
        reports.add(readConfiguration(parameterNumbers[paramIdx]));
      } catch(FlowException e) {
        System.out.printf("Failed to read parameter %s: %s\n", paramIdx, e.getMessage());
      }
    }
    return reports;
  }

  private ConfigurationReport readConfiguration(int parameterNumber) throws FlowException {
    System.out.printf("Reading configuration parameter %s...\n", parameterNumber);
    ZWaveSupportedCommand command = requestZWCommand(
        SendDataRequest.createSendDataRequest(
            options.getNodeId(),
            commandBuilder.buildGetParameterCommand(parameterNumber),
            nextFlowId()),
        CONFIGURATION_REPORT,
        options.getTimeout());
    return (ConfigurationReport) command;
  }

  private String formatValue(int size, int value) {
    switch(size) {
      case 1:
        return String.format("%02x", (byte) value);
      case 2:
        return String.format("%04x", (short) value);
      default:
        return String.format("%08x", value);
    }
  }

  public static void main(String... args) throws Exception {
    ZWaveCLI.main("node", "configuration", "read", "-d", "/dev/tty.usbmodem1421", "-n", "3", "-pn", "1-5");
  }
}
