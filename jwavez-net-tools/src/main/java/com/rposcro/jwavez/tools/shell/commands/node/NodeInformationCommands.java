package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.NodeDetailsCache;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.NodeDetails;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@ShellComponent
@ShellCommandGroup(CommandGroup.NODE)
public class NodeInformationCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeDetailsCache nodeDetailsCache;

    @Autowired
    private NodeInformationService nodeInformationService;

    @ShellMethod(value = "Shows or sets current node id", key = "id")
    public String showCurrentNodeId(@ShellOption(defaultValue = "-1") int nodeId) {
        if (nodeId <= 0) {
            return "Current node id is " + nodeScopeContext.getCurrentNodeId() + "\n";
        } else {
            nodeScopeContext.setCurrentNodeId(nodeId);
            return "Current node id changed to " + nodeId + "\n";
        }
    }

    @ShellMethod(value = "Shows current node information", key = "info")
    public String showNodeInformation() {
        NodeDetails nodeDetails = nodeDetailsCache.getNodeDetails(nodeScopeContext.getCurrentNodeId());
        return formatNodeDetails(nodeDetails);
    }

    @ShellMethod(value = "Fetches current node information", key = "fetch")
    public String fetchNodeInformation() throws SerialException {
        NodeDetails nodeDetails = nodeInformationService.fetchNodeInformation(nodeScopeContext.getCurrentNodeId());
        return formatNodeDetails(nodeDetails);
    }

    @ShellMethod(value = "Shows or sets node memo", key = "memo")
    public String nodeMemo(@ShellOption(defaultValue = ShellOption.NULL) String memo) throws SerialException {
        NodeDetails nodeDetails = nodeDetailsCache.getNodeDetails(nodeScopeContext.getCurrentNodeId());

        if (memo == null) {
            return "Memo for node " + nodeScopeContext.getCurrentNodeId() + " is: " + nodeDetails.getNodeMemo() + "\n";
        }

        nodeDetails.setNodeMemo(memo);
        return "Memo for node " + nodeScopeContext.getCurrentNodeId() + " changed to: " + memo + "\n";
    }

    @ShellMethodAvailability("fetch")
    public Availability checkRemoteAvailability() {

        if (ShellScope.NODE != shellContext.getScopeContext().getScope()) {
            return Availability.unavailable("Command not available in current scope");
        }

        return shellContext.getDevice() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }

    @ShellMethodAvailability({ "info", "memo" })
    public Availability checkLocalAvailability() {

        if (ShellScope.NODE != shellContext.getScopeContext().getScope()) {
            return Availability.unavailable("Command not available in current scope");
        }

        return nodeDetailsCache.isNodeKnown(nodeScopeContext.getCurrentNodeId()) ?
                Availability.available() :
                Availability.unavailable("Node " + nodeScopeContext.getCurrentNodeId() + " is not known yet, try to fetch details first");
    }

    private String formatNodeDetails(NodeDetails nodeDetails) {
        return String.format("Node id: %s\n"
                        + "Memo: %s\n"
                        + "Manufacturer id: 0x%04X\n"
                        + "Product type id: 0x%04X\n"
                        + "Product id: 0x%04X\n"
                        + "ZWave library type: 0x%02X\n"
                        + "ZWave protocol version: 0x%02X\n"
                        + "ZWave protocol sub version: 0x%02X\n"
                        + "Application version: 0x%02X\n"
                        + "Application sub version: 0x%02X\n"
                        + "Basic device class: %s\n"
                        + "Generic device class: %s\n"
                        + "Specific device class: %s\n"
                        + "Supported command classes: %s\n"
                , nodeDetails.getNodeId()
                , nodeDetails.getNodeMemo()
                , nodeDetails.getManufacturerId()
                , nodeDetails.getProductTypeId()
                , nodeDetails.getProductId()
                , nodeDetails.getZWaveLibraryType()
                , nodeDetails.getZWaveProtocolVersion()
                , nodeDetails.getZWaveProtocolSubVersion()
                , nodeDetails.getApplicationVersion()
                , nodeDetails.getApplicationSubVersion()
                , nodeDetails.getBasicDeviceClass()
                , nodeDetails.getGenericDeviceClass()
                , nodeDetails.getSpecificDeviceClass()
                , Stream.of(nodeDetails.getCommandClasses())
                        .map(cmdMeta ->
                            String.format("\n    %s(0x%02X) version %s", cmdMeta.getCommandClass(), cmdMeta.getCommandClass().getCode(), cmdMeta.getVersion())
                        )
                        .collect(Collectors.joining(", "))
        );
    }
}
