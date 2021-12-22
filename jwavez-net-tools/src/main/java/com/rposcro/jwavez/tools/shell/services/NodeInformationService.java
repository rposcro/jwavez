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
import com.rposcro.jwavez.core.model.NodeInfo;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeInformationService {

    @Autowired
    private SerialControllerManager serialControllerManager;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    public NodeInformation fetchNodeInformation(int nodeId) throws SerialException {
        NodeInformation nodeInformation = new NodeInformation();
        nodeInformation.setProductInformation(fetchProductInformation(nodeId));
        nodeInformation.setNodeId(nodeId);
        nodeInformation.setNodeMemo("Node " + nodeId);
        return nodeInformation;
    }

    public boolean pingNode(int nodeId) {
        try {
            fetchVersionReport(new NodeId(nodeId));
            return true;
        } catch(SerialException e) {
            return false;
        }
    }

    public List<NodeInformation> findMatchingNodes(int probeNodeId) {
        NodeInformation probeNode = nodeInformationCache.getNodeDetails(probeNodeId);
        List<NodeInformation> sameProductNodes = nodeInformationCache.getOrderedNodeList().stream()
                .filter(node -> nodesMatch(probeNode, node))
                .collect(Collectors.toList());
        return sameProductNodes;
    }

    public boolean nodesMatch(NodeInformation node1, NodeInformation node2) {
        NodeProductInformation product1 = node1.getProductInformation();
        NodeProductInformation product2 = node2.getProductInformation();
        return node1.getNodeId() != node2.getNodeId()
                && product1.getManufacturerId() == product2.getManufacturerId()
                && product1.getProductId() == product2.getProductId()
                && product1.getProductTypeId() == product2.getProductTypeId()
                && product1.getZWaveProtocolVersion() == product2.getZWaveProtocolVersion()
                && product1.getZWaveProtocolSubVersion() == product2.getZWaveProtocolSubVersion()
                && product1.getZWaveLibraryType() == product2.getZWaveLibraryType()
                && product1.getApplicationVersion() == product2.getApplicationVersion()
                && product1.getApplicationSubVersion() == product2.getApplicationSubVersion();
    }

    private NodeProductInformation fetchProductInformation(int nodeId) throws SerialException {
        final NodeId nodeID = new NodeId(nodeId);
        NodeInfo nodeInfo = fetchNodeInfo(nodeID);
        ManufacturerSpecificReport manufacturerReport = fetchManufacturerSpecificReport(nodeID);
        VersionReport versionReport = fetchVersionReport(nodeID);
        CommandClassMeta[] cmdClassMetas = fetchCommandClassMetadata(nodeID, nodeInfo.getCommandClasses());

        return NodeProductInformation.builder()
                .manufacturerId(manufacturerReport.getManufacturerId())
                .productId(manufacturerReport.getProductId())
                .productTypeId(manufacturerReport.getProductTypeId())
                .zWaveLibraryType(versionReport.getZWaveLibraryType())
                .zWaveProtocolVersion(versionReport.getZWaveProtocolVersion())
                .zWaveProtocolSubVersion(versionReport.getZWaveProtocolSubVersion())
                .applicationVersion(versionReport.getApplicationVersion())
                .applicationSubVersion(versionReport.getApplicationSubVersion())
                .basicDeviceClass(nodeInfo.getBasicDeviceClass())
                .genericDeviceClass(nodeInfo.getGenericDeviceClass())
                .specificDeviceClass(nodeInfo.getSpecificDeviceClass())
                .commandClasses(cmdClassMetas)
                .build();
    }

    private NodeInfo fetchNodeInfo(NodeId nodeID) throws SerialException {
        ApplicationUpdateCallback callback = serialControllerManager.runApplicationCommandFunction((executor ->
                executor.requestZWCallback(
                        RequestNodeInfoRequest.createRequestNodeInfoRequest(nodeID),
                        SerialCommand.APPLICATION_UPDATE,
                        SerialUtils.DEFAULT_TIMEOUT)
        ));
        return callback.getNodeInfo();
    }

    private ManufacturerSpecificReport fetchManufacturerSpecificReport(NodeId nodeID) throws SerialException {
        return serialControllerManager.runApplicationCommandFunction((executor ->
                executor.requestApplicationCommand(
                        nodeID,
                        new ManufacturerSpecificCommandBuilder().buildGetCommand(),
                        ManufacturerSpecificCommandType.MANUFACTURER_SPECIFIC_REPORT,
                        SerialUtils.DEFAULT_TIMEOUT)
        ));
    }

    private VersionReport fetchVersionReport(NodeId nodeID) throws SerialException {
        return serialControllerManager.runApplicationCommandFunction((executor ->
                executor.requestApplicationCommand(
                        nodeID,
                        new VersionCommandBuilder().buildGetCommand(),
                        VersionCommandType.VERSION_REPORT,
                        SerialUtils.DEFAULT_TIMEOUT)
        ));
    }

    private CommandClassMeta[] fetchCommandClassMetadata(NodeId nodeID, CommandClass[] commandClasses)
            throws SerialException {
        CommandClassMeta[] metas = new CommandClassMeta[commandClasses.length];
        for(int i = 0; i < commandClasses.length; i++) {
            CommandClass commandClass = commandClasses[i];
            if (commandClass.isMarker()) {
                metas[i] = new CommandClassMeta(commandClass, -1);
            } else {
                VersionCommandClassReport versionReport = serialControllerManager.runApplicationCommandFunction((executor ->
                        executor.requestApplicationCommand(
                                nodeID,
                                new VersionCommandBuilder().buildCommandClassGetCommand(commandClass),
                                VersionCommandType.VERSION_COMMAND_CLASS_REPORT,
                                SerialUtils.DEFAULT_TIMEOUT)
                ));
                metas[i] = new CommandClassMeta(commandClass, versionReport.getCommandClassVersion());
            }
        }
        return metas;
    }
}
