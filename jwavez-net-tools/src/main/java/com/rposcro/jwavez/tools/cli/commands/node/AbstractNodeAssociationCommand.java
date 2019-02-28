package com.rposcro.jwavez.tools.cli.commands.node;

import static com.rposcro.jwavez.core.commands.enums.AssociationCommandType.ASSOCIATION_REPORT;

import com.rposcro.jwavez.core.commands.controlled.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.tools.cli.commands.AbstractSendDataBasedCommand;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractNodeAssociationCommand extends AbstractSendDataBasedCommand {

  protected AssociationCommandBuilder associationCommandBuilder;

  protected AbstractNodeAssociationCommand() {
    associationCommandBuilder = new AssociationCommandBuilder();
  }

  protected void checkGroupAssociations(NodeId hostNodeId, int groupId, long timeout) {
    try {
      System.out.printf("Checking association group %s...\n", groupId);
      AssociationReport report = readGroupAssociations(hostNodeId, groupId, timeout);
      printAssociationReport(report);
    } catch (Exception e) {
      System.out.println("Failed to read association information due to: " + e.getMessage());
    }
  }

  protected AssociationReport readGroupAssociations(NodeId addresseeNodeId, int groupId, long timeout) throws FlowException {
    AssociationReport report = (AssociationReport) requestZWCommand(
        SendDataRequest.createSendDataRequest(
            addresseeNodeId,
            associationCommandBuilder.buildGetCommand(groupId),
            nextFlowId()),
        ASSOCIATION_REPORT,
        timeout);
    return report;
  }

  protected void printAssociationReport(AssociationReport report) {
    System.out.println(":: Report on group " + report.getGroupId());
    System.out.println("  max supported nodes count: " + report.getMaxNodesCountSupported());
    System.out.println("  nodes in group: " + Stream.of(report.getNodeIds())
        .map(nodeId -> String.format("%02x", nodeId.getId()))
        .collect(Collectors.joining(", ")));
    System.out.println();
  }
}
