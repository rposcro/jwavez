package com.rposcro.jwavez.tools.cli.commands.node;

import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeAssociationOptions;

public class NodeAssociationSetCommand extends AbstractNodeAssociationCommand {

  private NodeAssociationOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NodeAssociationOptions(args);
  }

  @Override
  public void execute() throws CommandExecutionException {
    connect(options);
    processSetRequest();
    checkGroupAssociations(options.getNodeId(), options.getAssociationGroupId(), options.getTimeout());
  }

  private void processSetRequest() {
    System.out.printf("Requesting association of node %s to group %s on node %s...\n", options.getAssociationNodeId(), options.getAssociationGroupId(), options.getNodeId());
    try {
      processSendDataRequest(
          options.getNodeId(),
          associationCommandBuilder.buildSetCommand(options.getAssociationGroupId(), options.getAssociationNodeId()));
      System.out.println("Association successful");
    } catch(FlowException e) {
      System.out.println("Association failed: " + e.getMessage());
    }
    System.out.println();
  }
}
