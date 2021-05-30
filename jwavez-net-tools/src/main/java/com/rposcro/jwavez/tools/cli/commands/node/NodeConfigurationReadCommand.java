package com.rposcro.jwavez.tools.cli.commands.node;

import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.cli.ZWaveCLI;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeConfigurationReadOptions;
import com.rposcro.jwavez.tools.cli.utils.ProcedureUtil;
import java.util.ArrayList;
import java.util.List;

public class NodeConfigurationReadCommand extends AbstractNodeConfigurationCommand {

  private NodeConfigurationReadOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NodeConfigurationReadOptions(args);
  }

  @Override
  public void execute() {
    System.out.println("Requesting configuration parameter(s) information...");
    ProcedureUtil.executeProcedure(this::runConfigurationRead);
    System.out.println("Configuration parameter(s) fetch finished");
  }

  private void runConfigurationRead() throws SerialException {
    connect(options);
    printReports(collectReports());
  }

  private void printReports(List<ConfigurationReport> reports) {
    System.out.println();
    reports.stream().forEachOrdered(this::printConfigurationReport);
  }

  private List<ConfigurationReport> collectReports() {
    int[] parameterNumbers = options.getParameterNumbers();
    List<ConfigurationReport> reports = new ArrayList<>(parameterNumbers.length);
    for (int paramIdx = 0; paramIdx < parameterNumbers.length; paramIdx++) {
      try {
        System.out.printf("Reading configuration parameter %s...\n", parameterNumbers[paramIdx]);
        reports.add(readConfiguration(options.getNodeId(), parameterNumbers[paramIdx], options.getTimeout()));
      } catch(SerialException e) {
        System.out.printf("Failed to read parameter %s: %s\n", paramIdx, e.getMessage());
      }
    }
    return reports;
  }

  public static void main(String... args) throws Exception {
    ZWaveCLI.main("node", "configuration", "read", "-d", "/dev/tty.usbmodem1421", "-n", "3", "-pn", "1-5");
  }
}
