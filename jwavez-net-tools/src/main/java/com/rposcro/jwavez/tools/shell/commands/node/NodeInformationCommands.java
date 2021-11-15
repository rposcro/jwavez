package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.models.NodeProductInformation;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
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
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationService nodeInformationService;

    @ShellMethod(value = "Fetch node information and select it", key = { "fetch" })
    public String fetchNodeInformation(int nodeId) throws SerialException {
        NodeInformation nodeInformation = nodeInformationService.fetchNodeInformation(nodeId);
        nodeInformationCache.persist();
        nodeScopeContext.setCurrentNodeId(nodeId);
        return formatVerboseNodeInfo(nodeInformation);
    }

    @ShellMethod(value = "Shows current node information", key = { "info" })
    public String showNodeInformation(@ShellOption(defaultValue = "false") boolean verbose) {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        return verbose ? formatVerboseNodeInfo(nodeInformation) : formatShortNodeInfo(nodeInformation);
    }

    @ShellMethod(value = "Shows or sets node memo", key = "memo")
    public String nodeMemo(@ShellOption(defaultValue = ShellOption.NULL) String memo) throws SerialException {
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeScopeContext.getCurrentNodeId());

        if (memo == null) {
            return "Memo for node " + nodeScopeContext.getCurrentNodeId() + " is: " + nodeInformation.getNodeMemo() + "\n";
        }

        nodeInformation.setNodeMemo(memo);
        nodeInformationCache.persist();
        return "Memo for node " + nodeScopeContext.getCurrentNodeId() + " changed to: " + memo + "\n";
    }

    @ShellMethodAvailability(value = { "fetch" })
    public Availability checkRemoteAvailability() {
        return shellContext.getDevice() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }

    @ShellMethodAvailability({ "info", "memo" })
    public Availability checkLocalAvailability() {
        return nodeScopeContext.isAnyNodeSelected() ?
                Availability.available() :
                Availability.unavailable("No node is selected in the working context, try to select or fetch one");
    }

    private String formatShortNodeInfo(NodeInformation nodeInformation) {
        return String.format("Node id: %s\nMemo: %s\n",
                nodeInformation.getNodeId(),
                nodeInformation.getNodeMemo());
    }

    private String formatVerboseNodeInfo(NodeInformation nodeInformation) {
        NodeProductInformation productInformation = nodeInformation.getProductInformation();
        return String.format("Node id: %s\n"
                        + "Memo: %s\n"
                        + "\n"
                        + "Manufacturer id: 0x%04X\n"
                        + "Product type id: 0x%04X\n"
                        + "Product id: 0x%04X\n"
                        + "\n"
                        + "ZWave library type: 0x%02X\n"
                        + "ZWave protocol version: 0x%02X\n"
                        + "ZWave protocol sub version: 0x%02X\n"
                        + "Application version: 0x%02X\n"
                        + "Application sub version: 0x%02X\n"
                        + "\n"
                        + "Basic device class: %s\n"
                        + "Generic device class: %s\n"
                        + "Specific device class: %s\n"
                        + "\n"
                        + "Supported command classes: %s\n"
                , nodeInformation.getNodeId()
                , nodeInformation.getNodeMemo()
                , productInformation.getManufacturerId()
                , productInformation.getProductTypeId()
                , productInformation.getProductId()
                , productInformation.getZWaveLibraryType()
                , productInformation.getZWaveProtocolVersion()
                , productInformation.getZWaveProtocolSubVersion()
                , productInformation.getApplicationVersion()
                , productInformation.getApplicationSubVersion()
                , productInformation.getBasicDeviceClass()
                , productInformation.getGenericDeviceClass()
                , productInformation.getSpecificDeviceClass()
                , Stream.of(productInformation.getCommandClasses())
                        .map(cmdMeta ->
                            String.format("\n    %s(0x%02X) version %s", cmdMeta.getCommandClass(), cmdMeta.getCommandClass().getCode(), cmdMeta.getVersion())
                        )
                        .collect(Collectors.joining(", "))
        );
    }
}
