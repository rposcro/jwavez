package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.association.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.core.commands.types.AssociationCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.SerialRequestFactory;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class NodeAssociationService {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private AssociationCommandBuilder associationCommandBuilder;

    @Autowired
    private SerialRequestFactory serialRequestFactory;

    public List<Integer> fetchGroupAssociations(int nodeId, int groupId) throws SerialException {
        final NodeId nodeID = NodeId.forId(nodeId);

        AssociationReport associationReport = (AssociationReport) serialCommunicationService.runApplicationCommandFunction((executor ->
                executor.requestApplicationCommand(
                        nodeID,
                        associationCommandBuilder.v1().buildGetCommand(groupId),
                        AssociationCommandType.ASSOCIATION_REPORT,
                        SerialUtils.DEFAULT_TIMEOUT)
        )).getAcquiredSupportedCommand();

        byte[] nodeIds = associationReport.getNodeIds();
        List<Integer> associatedNodes = IntStream.range(0, nodeIds.length)
                .map(idx -> nodeIds[idx] & 0xff)
                .sorted()
                .boxed()
                .collect(Collectors.toList());
        nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().replaceAllNodesAssociations(groupId, associatedNodes);
        return nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().findNodeAssociations(groupId);
    }

    public boolean sendAddAssociation(int nodeId, int groupId, int nodeIdToAssociate) throws SerialException {
        final NodeId nodeID = NodeId.forId(nodeId);
        boolean sendResult = serialCommunicationService.runBasicSynchronousFunction((executor) -> {
            ZWaveControlledCommand command = associationCommandBuilder.v1()
                    .buildSetCommand(groupId, nodeIdToAssociate);
            SendDataCallback callback = executor.requestCallbackFlow(
                    serialRequestFactory.networkTransportRequestBuilder().createSendDataRequest(nodeID, command, SerialUtils.nextFlowId()));
            return callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK;
        });

        if (sendResult) {
            List<Integer> fetchedAssociations = fetchGroupAssociations(nodeId, groupId);
            if (fetchedAssociations.contains(nodeIdToAssociate)) {
                nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().addNodeAssociation(groupId, nodeIdToAssociate);
                return true;
            }
        } else {
            throw new SerialException("Failed to deliver association set command to node!");
        }

        return false;
    }

    public boolean sendRemoveAssociation(int nodeId, int groupId, int nodeIdToRemove) throws SerialException {
        final NodeId nodeID = NodeId.forId(nodeId);
        boolean sendResult = serialCommunicationService.runBasicSynchronousFunction((executor) -> {
            ZWaveControlledCommand command = associationCommandBuilder.v1()
                    .buildRemoveCommand(groupId, nodeIdToRemove);
            SendDataCallback callback = executor.requestCallbackFlow(
                    serialRequestFactory.networkTransportRequestBuilder().createSendDataRequest(nodeID, command, SerialUtils.nextFlowId()));
            return callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK;
        });

        if (sendResult) {
            List<Integer> fetchedAssociations = fetchGroupAssociations(nodeId, groupId);
            if (!fetchedAssociations.contains(nodeIdToRemove)) {
                nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().removeNodeAssociation(groupId, nodeIdToRemove);
                return true;
            }
        } else {
            throw new SerialException("Failed to deliver association remove command to node!");
        }

        return false;
    }
}
