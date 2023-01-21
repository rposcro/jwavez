package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.core.commands.types.AssociationCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.shell.models.AssociationGroupMeta;
import com.rposcro.jwavez.tools.shell.models.NodeAssociationsInformation;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeAssociationService {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    public void updateOrCreateMeta(int nodeId, int groupId, String memo) {
        AssociationGroupMeta groupMeta = AssociationGroupMeta.builder()
                .groupId(groupId)
                .memo(memo)
                .build();
        nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().addOrReplaceGroupMeta(groupMeta);
    }

    public AssociationGroupMeta removeGroup(int nodeId, int groupId) {
        NodeAssociationsInformation associationsInformation = nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation();
        AssociationGroupMeta associationGroupMeta = associationsInformation.removeGroupMeta(groupId);
        associationsInformation.removeAllAssociations(groupId);
        return associationGroupMeta;
    }

    public void cloneGroupMetas(int sourceNodeId, int targetNodeId) {
        NodeInformation sourceNode = nodeInformationCache.getNodeDetails(sourceNodeId);
        NodeInformation targetNode = nodeInformationCache.getNodeDetails(targetNodeId);
        targetNode.getAssociationsInformation().wipeOutAll();

        sourceNode.getAssociationsInformation().getAssociationGroupsMetas().stream().forEach(meta -> {
            targetNode.getAssociationsInformation().addOrReplaceGroupMeta(meta);
        });
    }

    public List<Integer> fetchGroupAssociations(int nodeId, int groupId) throws SerialException {
        final NodeId nodeID = new NodeId(nodeId);

        AssociationReport associationReport = (AssociationReport) serialCommunicationService.runApplicationCommandFunction((executor ->
                executor.requestApplicationCommand(
                        nodeID,
                        new AssociationCommandBuilder().buildGetCommand(groupId),
                        AssociationCommandType.ASSOCIATION_REPORT,
                        SerialUtils.DEFAULT_TIMEOUT)
        )).getAcquiredSupportedCommand();

        List<Integer> associatedNodes = Arrays.stream(associationReport.getNodeIds())
                .map(id -> ((int) id.getId()) & 0xff)
                .sorted()
                .collect(Collectors.toList());
        nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().setAllAssociations(groupId, associatedNodes);
        return nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().findAssociations(groupId);
    }

    public boolean sendAddAssociation(int nodeId, int groupId, int nodeIdToAssociate) throws SerialException {
        final NodeId nodeID = new NodeId(nodeId);
        boolean sendResult = serialCommunicationService.runBasicSynchronousFunction((executor) -> {
            ZWaveControlledCommand command = new AssociationCommandBuilder()
                    .buildSetCommand(groupId, nodeIdToAssociate);
            SendDataCallback callback = executor.requestCallbackFlow(SendDataRequest.createSendDataRequest(nodeID, command, SerialUtils.nextFlowId()));
            return callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK;
        });

        if (sendResult) {
            List<Integer> fetchedAssociations = fetchGroupAssociations(nodeId, groupId);
            if (fetchedAssociations.contains(nodeIdToAssociate)) {
                nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().addAssociation(groupId, nodeIdToAssociate);
                return true;
            }
        } else {
            throw new SerialException("Failed to deliver association set command to node!");
        }

        return false;
    }

    public boolean sendRemoveAssociation(int nodeId, int groupId, int nodeIdToRemove) throws SerialException {
        final NodeId nodeID = new NodeId(nodeId);
        boolean sendResult = serialCommunicationService.runBasicSynchronousFunction((executor) -> {
            ZWaveControlledCommand command = new AssociationCommandBuilder()
                    .buildRemoveCommand(groupId, nodeIdToRemove);
            SendDataCallback callback = executor.requestCallbackFlow(SendDataRequest.createSendDataRequest(nodeID, command, SerialUtils.nextFlowId()));
            return callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK;
        });

        if (sendResult) {
            List<Integer> fetchedAssociations = fetchGroupAssociations(nodeId, groupId);
            if (!fetchedAssociations.contains(nodeIdToRemove)) {
                nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().removeAssociation(groupId, nodeIdToRemove);
                return true;
            }
        } else {
            throw new SerialException("Failed to deliver association set command to node!");
        }

        return false;
    }
}
