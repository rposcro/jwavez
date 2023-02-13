package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.configuration.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.model.BitLength;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.models.ParameterMeta;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeParameterService {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private ConfigurationCommandBuilder configurationCommandBuilder;

    public void updateOrCreateMeta(int nodeId, int paramNumber, int sizeInBits, String memo) {
        ParameterMeta parameterMeta = ParameterMeta.builder()
                .number(paramNumber)
                .sizeInBits(sizeInBits)
                .memo(memo)
                .build();
        nodeInformationCache.getNodeDetails(nodeId).getParametersInformation().addOrReplaceParameterMeta(parameterMeta);
    }

    public void cloneParametersMetas(int sourceNodeId, int targetNodeId) {
        NodeInformation sourceNode = nodeInformationCache.getNodeDetails(sourceNodeId);
        NodeInformation targetNode = nodeInformationCache.getNodeDetails(targetNodeId);
        targetNode.getParametersInformation().wipeOutAll();

        sourceNode.getParametersInformation().getParameterMetas().stream().forEach(meta -> {
            targetNode.getParametersInformation().addOrReplaceParameterMeta(meta);
        });
    }

    public Long fetchParameterValue(int nodeId, int paramNumber) throws SerialException {
        final NodeId nodeID = new NodeId(nodeId);
        ConfigurationReport configurationReport = (ConfigurationReport) serialCommunicationService.runApplicationCommandFunction((executor ->
                executor.requestApplicationCommand(
                        nodeID,
                        configurationCommandBuilder.v1().buildGetParameterCommand(paramNumber),
                        ConfigurationCommandType.CONFIGURATION_REPORT,
                        SerialUtils.DEFAULT_TIMEOUT)
        )).getAcquiredSupportedCommand();

        long paramValue = ((long) configurationReport.getValue()) & 0xffffffff;
        nodeInformationCache.getNodeDetails(nodeId).getParametersInformation().setParameterValue(paramNumber, paramValue);
        return paramValue;
    }

    public boolean sendParameterValue(int nodeId, int paramNumber, long requestedValue) throws SerialException {
        final NodeId nodeID = new NodeId(nodeId);
        final ParameterMeta parameterMeta = nodeInformationCache.getNodeDetails(nodeId).getParametersInformation().findParameterMeta(paramNumber);
        boolean sendResult = serialCommunicationService.runBasicSynchronousFunction((executor) -> {
            ZWaveControlledCommand command = configurationCommandBuilder.v1()
                    .buildSetParameterCommand(paramNumber, (int) requestedValue, BitLength.ofBytesNumber(parameterMeta.getSizeInBytes()));
            SendDataCallback callback = executor.requestCallbackFlow(SendDataRequest.createSendDataRequest(nodeID, command, SerialUtils.nextFlowId()));
            return callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK;
        });

        if (sendResult) {
            long fetchedValue = fetchParameterValue(nodeId, paramNumber);
            if (fetchedValue == requestedValue) {
                nodeInformationCache.getNodeDetails(nodeId).getParametersInformation().setParameterValue(paramNumber, requestedValue);
                return true;
            }
        } else {
            throw new SerialException("Failed to deliver parameter set command to node!");
        }

        return false;
    }
}
