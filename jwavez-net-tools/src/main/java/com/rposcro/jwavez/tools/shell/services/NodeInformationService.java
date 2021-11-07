package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.builders.ManufacturerSpecificCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.VersionCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.manufacturerspecific.ManufacturerSpecificReport;
import com.rposcro.jwavez.core.commands.supported.version.VersionCommandClassReport;
import com.rposcro.jwavez.core.commands.supported.version.VersionReport;
import com.rposcro.jwavez.core.commands.types.ManufacturerSpecificCommandType;
import com.rposcro.jwavez.core.commands.types.VersionCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationUpdateCallback;
import com.rposcro.jwavez.serial.frames.requests.RequestNodeInfoRequest;
import com.rposcro.jwavez.tools.shell.models.NodeDetails;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeInformationService {

    @Autowired
    private SerialControllerManager serialControllerManager;

    @Autowired
    private NodeDetailsCache nodeDetailsCache;

    public NodeDetails fetchNodeInformation(int nodeId) throws SerialException {
        final NodeId nodeID = new NodeId(nodeId);

        ApplicationUpdateCallback nodeInfoCallback = serialControllerManager.runApplicationCommandFunction((executor ->
            executor.requestZWCallback(
                    RequestNodeInfoRequest.createRequestNodeInfoRequest(nodeID),
                    SerialCommand.APPLICATION_UPDATE,
                    SerialUtils.DEFAULT_TIMEOUT)
        ));

        ManufacturerSpecificReport manufacturerReport = serialControllerManager.runApplicationCommandFunction((executor ->
            executor.requestApplicationCommand(
                    nodeID,
                    new ManufacturerSpecificCommandBuilder().buildGetCommand(),
                    ManufacturerSpecificCommandType.MANUFACTURER_SPECIFIC_REPORT,
                    SerialUtils.DEFAULT_TIMEOUT)
        ));

        VersionReport versionReport = serialControllerManager.runApplicationCommandFunction((executor ->
            executor.requestApplicationCommand(
                    nodeID,
                    new VersionCommandBuilder().buildGetCommand(),
                    VersionCommandType.VERSION_REPORT,
                    SerialUtils.DEFAULT_TIMEOUT)
        ));

        NodeDetails.CommandClassMeta[] metas = fetchCommandClassMetadata(nodeID, nodeInfoCallback.getNodeInfo().getCommandClasses());
        NodeDetails nodeDetails = constructNodeDetails(nodeId, manufacturerReport, versionReport, nodeInfoCallback, metas);
        nodeDetailsCache.cacheNodeDetails(nodeDetails);
        return nodeDetails;
    }

    private NodeDetails.CommandClassMeta[] fetchCommandClassMetadata(NodeId nodeID, CommandClass[] commandClasses)
            throws SerialException {
        NodeDetails.CommandClassMeta[] metas = new NodeDetails.CommandClassMeta[commandClasses.length];
        for(int i = 0; i < commandClasses.length; i++) {
            CommandClass commandClass = commandClasses[i];
            VersionCommandClassReport versionReport = serialControllerManager.runApplicationCommandFunction((executor ->
                    executor.requestApplicationCommand(
                            nodeID,
                            new VersionCommandBuilder().buildCommandClassGetCommand(commandClass),
                            VersionCommandType.VERSION_COMMAND_CLASS_REPORT,
                            SerialUtils.DEFAULT_TIMEOUT)
            ));
            metas[i] = new NodeDetails.CommandClassMeta(commandClass, versionReport.getCommandClassVersion());
        }
        return metas;
    }

    private NodeDetails constructNodeDetails(
            int nodeId,
            ManufacturerSpecificReport manufacturerReport,
            VersionReport versionReport,
            ApplicationUpdateCallback nodeInfoCallback,
            NodeDetails.CommandClassMeta[] metas) {
        String memo = nodeDetailsCache.isNodeKnown(nodeId) ? nodeDetailsCache.getNodeDetails(nodeId).getNodeMemo()
                : "ZWave Node " + nodeId;
        return NodeDetails.builder()
                .nodeId(nodeId)
                .nodeMemo(memo)
                .manufacturerId(manufacturerReport.getManufacturerId())
                .productId(manufacturerReport.getProductId())
                .productTypeId(manufacturerReport.getProductTypeId())
                .zWaveLibraryType(versionReport.getZWaveLibraryType())
                .zWaveProtocolVersion(versionReport.getZWaveProtocolVersion())
                .zWaveProtocolSubVersion(versionReport.getZWaveProtocolSubVersion())
                .applicationVersion(versionReport.getApplicationVersion())
                .applicationSubVersion(versionReport.getApplicationSubVersion())
                .basicDeviceClass(nodeInfoCallback.getNodeInfo().getBasicDeviceClass())
                .genericDeviceClass(nodeInfoCallback.getNodeInfo().getGenericDeviceClass())
                .specificDeviceClass(nodeInfoCallback.getNodeInfo().getSpecificDeviceClass())
                .commandClasses(metas)
                .build();
    }
}
