package com.rposcro.jwavez.tools.cli.commands.node;

import static com.rposcro.jwavez.core.commands.enums.AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT;
import static com.rposcro.jwavez.core.commands.enums.AssociationCommandType.ASSOCIATION_REPORT;

import com.rposcro.jwavez.core.commands.controlled.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.association.AssociationGroupingsReport;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.serial.probe.transactions.SendDataTransaction;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.DefaultNodeBasedOptions;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodeAssociationInfoCommand extends AbstractNodeCommand {

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
    AssociationReport[] reports = collectReports();
    printReport(reports);
  }

  private void printReport(AssociationReport[] reports) {
    System.out.println();
    Stream.of(reports)
        .forEachOrdered(report -> {
          System.out.println(":: Report on group " + report.getGroupId());
          System.out.println("  max supported nodes count: " + report.getMaxNodesCountSupported());
          System.out.println("  nodes in group: " + Stream.of(report.getNodeIds())
              .map(nodeId -> String.format("%02X", nodeId.getId()))
              .collect(Collectors.joining(", ")));
          System.out.println();
        });
  }

  private AssociationReport[] collectReports() throws CommandExecutionException {
    int groupsCount = readGroupingsCount();
    AssociationReport[] reports = new AssociationReport[groupsCount];
    for (int group = 1; group <= groupsCount; group++) {
      reports[group - 1] = readGroupAssociations(group);
    }
    return reports;
  }

  private int readGroupingsCount() throws CommandExecutionException {
    System.out.println("Checking association groups availabilities...");
    SendDataTransaction transaction = new SendDataTransaction(options.getNodeId(), commandBuilder.buildGetSupportedGroupingsCommand(), false);
    AssociationGroupingsReport report = (AssociationGroupingsReport) requestZWaveCommand(transaction, ASSOCIATION_GROUPINGS_REPORT, options.getTimeout(DEFAULT_CALLBACK_TIMEOUT));
    System.out.println("Available groups count: " + report.getGroupsCount());
    return report.getGroupsCount();
  }

  private AssociationReport readGroupAssociations(int groupNumber) throws CommandExecutionException {
    System.out.println(String.format("Checking association group %s...", groupNumber));
    SendDataTransaction transaction = new SendDataTransaction(options.getNodeId(), commandBuilder.buildGetCommand(groupNumber), false);
    AssociationReport report = (AssociationReport) requestZWaveCommand(transaction, ASSOCIATION_REPORT, options.getTimeout(DEFAULT_CALLBACK_TIMEOUT));
    return report;
  }
}
