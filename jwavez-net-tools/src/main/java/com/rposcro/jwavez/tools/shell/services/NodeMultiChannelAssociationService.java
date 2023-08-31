package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannelassociation.MultiChannelAssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.multichannelassociation.MultiChannelAssociationReport;
import com.rposcro.jwavez.core.commands.supported.multichannelassociation.MultiChannelAssociationReportInterpreter;
import com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType;
import com.rposcro.jwavez.core.model.EndPointAddress;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.SerialRequestFactory;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.shell.models.EndPointMark;
import com.rposcro.jwavez.tools.shell.models.NodeAssociationsInformation;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private SerialRequestFactory serialRequestFactory;

    public NodeAssociationsInformation fetchMultiChannelAssociations(int nodeId, int groupId) throws SerialException {
        final NodeId nodeID = NodeId.forId(nodeId);

        MultiChannelAssociationReport associationReport =
                (MultiChannelAssociationReport) serialCommunicationService.runApplicationCommandFunction((executor ->
                        executor.requestApplicationCommand(
                                nodeID,
                                associationCommandBuilder.v2().buildGetCommand(groupId),
                                MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT,
                                SerialUtils.DEFAULT_TIMEOUT)
                )).getAcquiredSupportedCommand();
        MultiChannelAssociationReportInterpreter interpreter = associationReport.interpreter();

        List<Integer> associatedNodes = interpreter.nodeIdList();
        List<EndPointMark> associatedEndPoints = interpreter.endPointAddressList().stream()
                .map(address -> new EndPointMark(address.getNodeId(), address.getEndPointId()))
                .collect(Collectors.toList());

        NodeAssociationsInformation associationsInformation = nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation();
        associationsInformation.replaceAllNodesAssociations(groupId, associatedNodes);
        associationsInformation.replaceAllEndPointsAssociations(groupId, associatedEndPoints);
        return associationsInformation;
    }

    public boolean sendAddAssociation(int nodeId, int groupId, EndPointMark addressToAssociate) throws SerialException {
        final NodeId nodeID = NodeId.forId(nodeId);
        boolean sendResult = serialCommunicationService.runBasicSynchronousFunction((executor) -> {
            ZWaveControlledCommand command = associationCommandBuilder.v2()
                    .buildSetCommand(groupId, new EndPointAddress(addressToAssociate.getNodeId(), addressToAssociate.getEndPointId()));
            SendDataCallback callback = executor.requestCallbackFlow(
                    serialRequestFactory.networkTransportRequestBuilder().createSendDataRequest(nodeID, command, SerialUtils.nextFlowId()));
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

    public boolean sendRemoveAssociation(int nodeId, int groupId, EndPointMark endPointToRemove) throws SerialException {
        final NodeId nodeID = NodeId.forId(nodeId);
        boolean sendResult = serialCommunicationService.runBasicSynchronousFunction((executor) -> {
            ZWaveControlledCommand command = associationCommandBuilder.v2()
                    .buildRemoveCommand(groupId, new EndPointAddress(endPointToRemove.getNodeId(), endPointToRemove.getEndPointId()));
            SendDataCallback callback = executor.requestCallbackFlow(
                    serialRequestFactory.networkTransportRequestBuilder().createSendDataRequest(nodeID, command, SerialUtils.nextFlowId()));
            return callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK;
        });

        if (sendResult) {
            NodeAssociationsInformation fetchedAssociations = fetchMultiChannelAssociations(nodeId, groupId);
            if (!fetchedAssociations.findEndPointAssociations(groupId).contains(endPointToRemove)) {
                nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().removeEndPointAssociation(groupId, endPointToRemove);
                return true;
            }
        } else {
            throw new SerialException("Failed to deliver association remove command to node!");
        }

        return false;
    }
}
