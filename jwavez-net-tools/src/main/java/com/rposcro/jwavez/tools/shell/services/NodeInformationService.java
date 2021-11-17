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
import com.rposcro.jwavez.tools.shell.models.CommandClassMeta;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.models.NodeProductInformation;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeInformationService {

    @Autowired
    private SerialControllerManager serialControllerManager;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    public NodeInformation fetchNodeInformation(int nodeId) throws SerialException {
        NodeInformation nodeInformation = getOrConstructNodeInformation(nodeId);
        nodeInformation.setProductInformation(fetchProductInformation(nodeId));
        nodeInformation.setNodeId(nodeId);
        nodeInformation.setNodeMemo("Node " + nodeId);
        nodeInformationCache.cacheNodeDetails(nodeInformation);
        return nodeInformation;
    }

    public NodeInformation discoverSameDevice(int probeNodeId) {
        NodeProductInformation probeProduct = nodeInformationCache.getNodeDetails(probeNodeId).getProductInformation();
        NodeInformation sameProductNode = nodeInformationCache.getOrderedNodeList().stream()
                .filter(node ->
                    node.getNodeId() != probeNodeId
                    && probeProduct.getManufacturerId() == node.getProductInformation().getManufacturerId()
                    && probeProduct.getProductId() == node.getProductInformation().getProductId()
                    && probeProduct.getProductTypeId() == node.getProductInformation().getProductTypeId()
                    && probeProduct.getZWaveProtocolVersion() == node.getProductInformation().getZWaveProtocolVersion()
                    && probeProduct.getZWaveProtocolSubVersion() == node.getProductInformation().getZWaveProtocolSubVersion()
                    && probeProduct.getZWaveLibraryType() == node.getProductInformation().getZWaveLibraryType()
                    && probeProduct.getApplicationVersion() == node.getProductInformation().getApplicationVersion()
                    && probeProduct.getApplicationSubVersion() == node.getProductInformation().getApplicationSubVersion())
                .findFirst()
                .orElse(null);
        return sameProductNode;
    }

    private NodeProductInformation fetchProductInformation(int nodeId) throws SerialException {
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

        CommandClassMeta[] cmdClassMetas = fetchCommandClassMetadata(nodeID, nodeInfoCallback.getNodeInfo().getCommandClasses());

        return NodeProductInformation.builder()
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
                .commandClasses(cmdClassMetas)
                .build();
    }

    private NodeInformation getOrConstructNodeInformation(int nodeId) {
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        if (nodeInformation == null) {
            nodeInformation = new NodeInformation();
        }
        return nodeInformation;
    }

    private CommandClassMeta[] fetchCommandClassMetadata(NodeId nodeID, CommandClass[] commandClasses)
            throws SerialException {
        CommandClassMeta[] metas = new CommandClassMeta[commandClasses.length];
        for(int i = 0; i < commandClasses.length; i++) {
            CommandClass commandClass = commandClasses[i];
            VersionCommandClassReport versionReport = serialControllerManager.runApplicationCommandFunction((executor ->
                    executor.requestApplicationCommand(
                            nodeID,
                            new VersionCommandBuilder().buildCommandClassGetCommand(commandClass),
                            VersionCommandType.VERSION_COMMAND_CLASS_REPORT,
                            SerialUtils.DEFAULT_TIMEOUT)
            ));
            metas[i] = new CommandClassMeta(commandClass, versionReport.getCommandClassVersion());
        }
        return metas;
    }
}
