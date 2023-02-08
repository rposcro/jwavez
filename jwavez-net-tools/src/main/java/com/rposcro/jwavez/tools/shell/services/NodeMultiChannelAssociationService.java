package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannelassociation.MultiChannelAssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.multichannelassociation.MultiChannelAssociationReport;
import com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType;
import com.rposcro.jwavez.core.model.EndPointId;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.shell.models.EndPointAddress;
import com.rposcro.jwavez.tools.shell.models.NodeAssociationsInformation;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeMultiChannelAssociationService {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private MultiChannelAssociationCommandBuilder associationCommandBuilder;

    public NodeAssociationsInformation fetchMultiChannelAssociations(int nodeId, int groupId) throws SerialException {
        final NodeId nodeID = new NodeId(nodeId);

        MultiChannelAssociationReport associationReport =
                (MultiChannelAssociationReport) serialCommunicationService.runApplicationCommandFunction((executor ->
                        executor.requestApplicationCommand(
                                nodeID,
                                associationCommandBuilder.v2().buildGetCommand(groupId),
                                MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT,
                                SerialUtils.DEFAULT_TIMEOUT)
        )).getAcquiredSupportedCommand();

        List<Integer> associatedNodes = Arrays.stream(associationReport.getNodeIds())
                .map(id -> ((int) id.getId()) & 0xff)
                .sorted()
                .collect(Collectors.toList());
        List<EndPointAddress> associatedEndPoints = Arrays.stream(associationReport.getEndPointIds())
                .map(endPoint -> new EndPointAddress(endPoint.getNodeId(), endPoint.getEndPointId()))
                .sorted()
                .collect(Collectors.toList());

        NodeAssociationsInformation associationsInformation = nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation();
        associationsInformation.replaceAllNodesAssociations(groupId, associatedNodes);
        associationsInformation.replaceAllEndPointsAssociations(groupId, associatedEndPoints);
        return associationsInformation;
    }

    public boolean sendAddAssociation(int nodeId, int groupId, EndPointAddress addressToAssociate) throws SerialException {
        final NodeId nodeID = new NodeId(nodeId);
        boolean sendResult = serialCommunicationService.runBasicSynchronousFunction((executor) -> {
            ZWaveControlledCommand command = associationCommandBuilder.v2()
                    .buildSetCommand(groupId, new EndPointId(addressToAssociate.getNodeId(), addressToAssociate.getEndPointId()));
            SendDataCallback callback = executor.requestCallbackFlow(SendDataRequest.createSendDataRequest(nodeID, command, SerialUtils.nextFlowId()));
            return callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK;
        });

        if (sendResult) {
            NodeAssociationsInformation fetchedAssociations = fetchMultiChannelAssociations(nodeId, groupId);
            if (fetchedAssociations.findEndPointAssociations(groupId).contains(addressToAssociate)) {
                nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().addEndPointAssociation(groupId, addressToAssociate);
                return true;
            }
        } else {
            throw new SerialException("Failed to deliver association set command to node!");
        }

        return false;
    }

    public boolean sendRemoveAssociation(int nodeId, int groupId, EndPointAddress addressToRemove) throws SerialException {
        final NodeId nodeID = new NodeId(nodeId);
        boolean sendResult = serialCommunicationService.runBasicSynchronousFunction((executor) -> {
            ZWaveControlledCommand command = associationCommandBuilder.v2()
                    .buildRemoveCommand(groupId, new EndPointId(addressToRemove.getNodeId(), addressToRemove.getEndPointId()));
            SendDataCallback callback = executor.requestCallbackFlow(SendDataRequest.createSendDataRequest(nodeID, command, SerialUtils.nextFlowId()));
            return callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK;
        });

        if (sendResult) {
            NodeAssociationsInformation fetchedAssociations = fetchMultiChannelAssociations(nodeId, groupId);
            if (!fetchedAssociations.findEndPointAssociations(groupId).contains(addressToRemove)) {
                nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().removeEndPointAssociation(groupId, addressToRemove);
                return true;
            }
        } else {
            throw new SerialException("Failed to deliver association remove command to node!");
        }

        return false;
    }
}
