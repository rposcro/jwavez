package com.rposcro.jwavez.tools.cli.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeAssociationOptions;
import com.rposcro.jwavez.tools.cli.utils.ProcedureUtil;

public class NodeAssociationSetCommand extends AbstractNodeAssociationCommand {

    private NodeAssociationOptions options;

    @Override
    public void configure(String[] args) throws CommandOptionsException {
        options = new NodeAssociationOptions(args);
    }

    @Override
    public void execute() {
        System.out.printf("Requesting association of node %s to group %s on node %s...\n", options.getAssociationNodeId(), options.getAssociationGroupId(), options.getNodeId());
        ProcedureUtil.executeProcedure(this::processSetRequest);
        checkGroupAssociations(options.getNodeId(), options.getAssociationGroupId(), options.getTimeout());
        System.out.println("Association finished");
    }

    private void processSetRequest() throws SerialException {
        connect(options);
        processSendDataRequest(
                options.getNodeId(),
                associationCommandBuilder.v1().buildSetCommand(options.getAssociationGroupId(), options.getAssociationNodeId()));
    }
}
