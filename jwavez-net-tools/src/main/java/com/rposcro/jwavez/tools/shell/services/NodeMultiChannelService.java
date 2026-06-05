package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannel.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCapabilityReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelEndPointReport;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.shell.models.NodeMultiChannelInformation;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class NodeMultiChannelService {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    @Autowired
    private MultiChannelCommandBuilder multiChannelCommandBuilder;

    public NodeMultiChannelInformation fetchMultiChannelAvailabilities(int nodeId) throws SerialException {
        final NodeId nodeID = NodeId.forId(nodeId);
        MultiChannelEndPointReport endPointsReport = (MultiChannelEndPointReport) serialCommunicationService.runApplicationCommandFunction((executor ->
                executor.requestApplicationCommand(
                        nodeID,
                        multiChannelCommandBuilder.v3().buildEndPointGetCommand(),
                        MultiChannelCommandType.MULTI_CHANNEL_END_POINT_REPORT,
                        SerialUtils.DEFAULT_TIMEOUT)
        )).getAcquiredSupportedCommand();
        log.debug("Received end points report: {}", endPointsReport);

        MultiChannelCapabilityReport[] capabilityReports = new MultiChannelCapabilityReport[endPointsReport.getEndPointsCount()];
        for (int endPointIdx = 1; endPointIdx <= endPointsReport.getEndPointsCount(); endPointIdx++) {
            log.debug("Querying for capability report: {}", endPointIdx);
            ZWaveControlledCommand command = multiChannelCommandBuilder.v3().buildCapabilityGetCommand((byte) endPointIdx);
            MultiChannelCapabilityReport capabilityReport = (MultiChannelCapabilityReport) serialCommunicationService.runApplicationCommandFunction((executor ->
                executor.requestApplicationCommand(
                    nodeID,
                    command,
                    MultiChannelCommandType.MULTI_CHANNEL_CAPABILITY_REPORT,
                    SerialUtils.DEFAULT_TIMEOUT)
            )).getAcquiredSupportedCommand();
            capabilityReports[endPointIdx - 1] = capabilityReport;
        }

        return NodeMultiChannelInformation.builder()
            .endPointReport(endPointsReport)
            .capabilityReports(capabilityReports)
            .build();
    }
}
