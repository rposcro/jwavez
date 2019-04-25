package com.rposcro.jwavez.tools.cli.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeAssociationOptions;

public class NodeAssociationRemoveCommand extends AbstractNodeAssociationCommand {

  private NodeAssociationOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NodeAssociationOptions(args);
  }

  @Override
  public void execute() throws CommandExecutionException {
    connect(options);
    processRemoveRequest();
    checkGroupAssociations(options.getNodeId(), options.getAssociationGroupId(), options.getTimeout());
  }

  private void processRemoveRequest() {
    System.out.printf("Requesting association removal of node %s to group %s on node %s...\n", options.getAssociationNodeId(), options.getAssociationGroupId(), options.getNodeId());
    try {
      processSendDataRequest(
          options.getNodeId(),
          associationCommandBuilder.buildRemoveCommand(options.getAssociationGroupId(), options.getAssociationNodeId()));
    } catch(SerialException e) {
      System.out.println("Association removal failed: " + e.getMessage());
    }
    System.out.println("Association removal successful");
    System.out.println();
  }
}
