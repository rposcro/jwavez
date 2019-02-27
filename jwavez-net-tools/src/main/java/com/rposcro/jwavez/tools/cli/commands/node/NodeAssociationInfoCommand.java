package com.rposcro.jwavez.tools.cli.commands.node;

import static com.rposcro.jwavez.core.commands.enums.AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT;
import static com.rposcro.jwavez.core.commands.enums.AssociationCommandType.ASSOCIATION_REPORT;

import com.rposcro.jwavez.core.commands.controlled.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.association.AssociationGroupingsReport;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.tools.cli.ZWaveCLI;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.DefaultNodeBasedOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodeAssociationInfoCommand extends SendDataBasedCommand {

  private DefaultNodeBasedOptions options;
  private AssociationCommandBuilder commandBuilder;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new DefaultNodeBasedOptions(args);
    commandBuilder = new AssociationCommandBuilder();
  }

  @Override
  public void execute() throws CommandExecutionException {
    connect(options);
    System.out.println("Requesting node association information...");
    List<AssociationReport> reports = collectReports();
    printReport(reports);
  }

  private void printReport(List<AssociationReport> reports) {
    System.out.println();
    reports.stream()
        .forEachOrdered(report -> {
          System.out.println(":: Report on group " + report.getGroupId());
          System.out.println("  max supported nodes count: " + report.getMaxNodesCountSupported());
          System.out.println("  nodes in group: " + Stream.of(report.getNodeIds())
              .map(nodeId -> String.format("%02x", nodeId.getId()))
              .collect(Collectors.joining(", ")));
          System.out.println();
        });
  }

  private List<AssociationReport> collectReports() {
    int groupsCount;
    try {
      groupsCount = readGroupingsCount();
    } catch(FlowException e) {
      System.out.printf("Failed to read groupings count: %s\n", e.getMessage());
      return Collections.emptyList();
    }

    List<AssociationReport> reports = new ArrayList<>(groupsCount);
    for (int groupIdx = 1; groupIdx <= groupsCount; groupIdx++) {
      try {
        reports.add(readGroupAssociations(groupIdx));
      } catch(FlowException e) {
        System.out.printf("Failed to read group %s: %s\n", groupIdx, e.getMessage());
      }
    }
    return reports;
  }

  private int readGroupingsCount() throws FlowException {
    System.out.println("Checking association groups availabilities...");
    AssociationGroupingsReport report = (AssociationGroupingsReport) requestZWCommand(
        SendDataRequest.createSendDataRequest(
            options.getNodeId(),
            commandBuilder.buildGetSupportedGroupingsCommand(),
            nextFlowId()),
        ASSOCIATION_GROUPINGS_REPORT,
        options.getTimeout());
    System.out.println("Available groups count: " + report.getGroupsCount());
    return report.getGroupsCount();
  }

  private AssociationReport readGroupAssociations(int groupNumber) throws FlowException {
    System.out.printf("Checking association group %s...\n", groupNumber);
    AssociationReport report = (AssociationReport) requestZWCommand(
        SendDataRequest.createSendDataRequest(
            options.getNodeId(),
            commandBuilder.buildGetCommand(groupNumber),
            nextFlowId()),
        ASSOCIATION_REPORT,
        options.getTimeout());
    return report;
  }

  public static void main(String... args) throws Exception {
    ZWaveCLI.main("node", "association", "info", "-d", "/dev/tty.usbmodem1421", "-n", "3");
  }
}
