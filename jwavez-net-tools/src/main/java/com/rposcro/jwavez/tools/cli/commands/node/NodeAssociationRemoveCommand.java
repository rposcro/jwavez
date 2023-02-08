package com.rposcro.jwavez.tools.cli.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeAssociationOptions;
import com.rposcro.jwavez.tools.cli.utils.ProcedureUtil;

public class NodeAssociationRemoveCommand extends AbstractNodeAssociationCommand {

    private NodeAssociationOptions options;

    @Override
    public void configure(String[] args) throws CommandOptionsException {
        options = new NodeAssociationOptions(args);
    }

    @Override
    public void execute() {
        System.out.printf("Requesting association removal of node %s to group %s on node %s...\n", options.getAssociationNodeId(), options.getAssociationGroupId(), options.getNodeId());
        ProcedureUtil.executeProcedure(this::processRemoveRequest);
        checkGroupAssociations(options.getNodeId(), options.getAssociationGroupId(), options.getTimeout());
        System.out.println("Association removal finished");
    }

    private void processRemoveRequest() throws SerialException {
        connect(options);
        processSendDataRequest(
                options.getNodeId(),
                associationCommandBuilder.v1().buildRemoveCommand(options.getAssociationGroupId(), options.getAssociationNodeId()));
    }
}
