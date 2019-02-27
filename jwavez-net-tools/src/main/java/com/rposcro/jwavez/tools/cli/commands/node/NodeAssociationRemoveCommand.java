package com.rposcro.jwavez.tools.cli.commands.node;

import static com.rposcro.jwavez.core.commands.enums.AssociationCommandType.ASSOCIATION_REPORT;

import com.rposcro.jwavez.core.commands.controlled.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.serial.probe.transactions.SendDataTransaction;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeAssociationOptions;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodeAssociationRemoveCommand extends AbstractNodeCommand2 {

  private NodeAssociationOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NodeAssociationOptions(args);
  }

  @Override
  public void execute() throws CommandExecutionException {
    connect(options);
    processSetRequest();
    checkGroupAssociations();
  }

  private void processSetRequest() throws CommandExecutionException {
    System.out.println(String.format("Removing association of node %s from group %s...", options.getAssociationNodeId(), options.getAssociationGroupId()));
    SendDataTransaction transaction = new SendDataTransaction(
        options.getNodeId(),
        new AssociationCommandBuilder().buildRemoveCommand(options.getAssociationGroupId(), options.getAssociationNodeId()));
    executeTransaction(transaction, options.getTimeout(DEFAULT_TRANSACTION_TIMEOUT));
    System.out.println("Association removed successfully");
    System.out.println();
  }

  private void checkGroupAssociations() {
    try {
      int groupId = options.getAssociationGroupId();
      System.out.println(String.format("Checking association group %s...", groupId));
      AssociationCommandBuilder commandBuilder = new AssociationCommandBuilder();
      SendDataTransaction transaction = new SendDataTransaction(options.getNodeId(), commandBuilder.buildGetCommand(groupId), false);
      AssociationReport report = (AssociationReport) requestZWaveCommand(transaction, ASSOCIATION_REPORT, options.getTimeout(DEFAULT_CALLBACK_TIMEOUT));
      System.out.println(":: Report on group " + report.getGroupId());
      System.out.println("  max supported nodes count: " + report.getMaxNodesCountSupported());
      System.out.println("  nodes in group: " + Stream.of(report.getNodeIds())
          .map(nodeId -> String.format("%02X", nodeId.getId()))
          .collect(Collectors.joining(", ")));
      System.out.println();
    } catch (Exception e) {
      System.out.println("Failed to read association information due to: " + e.getMessage());
    }
  }
}
