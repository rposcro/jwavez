package com.rposcro.jwavez.tools.shell.commands.network;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.DongleNetworkInformation;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.DongleInformationService;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup(CommandGroup.NETWORK)
public class NodesCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private DongleInformationService dongleInformationService;

    @Autowired
    private NodeInformationService nodeInformationService;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private ConsoleAccessor console;

    @ShellMethod(value = "Check consistency of nodes on the network", key={ "check nodes" })
    public String checkNodes() throws SerialException {
        List<NodeInformation> cachedNodes = nodeInformationCache.getOrderedNodeList();
        console.flushLine(String.format("Cache holds information about %s node(s)", cachedNodes.size()));

        DongleNetworkInformation dongleNetworkInformation = dongleInformationService.collectDongleNetworkInformation();
        console.flushLine(String.format("Dongle knows about %s node(s)", dongleNetworkInformation.getNodeIds().length - 1));

        HashMap<Integer, NodeReport> reportsMap = new HashMap<>();
        reportsMap.putAll(cachedNodes.stream()
                .map(node -> {
                    NodeReport report = new NodeReport();
                    report.nodeId = node.getNodeId();
                    report.cacheKnows = true;
                    return report;
                })
                .collect(Collectors.toMap(report -> report.nodeId, Function.identity())));
        Arrays.stream(dongleNetworkInformation.getNodeIds())
                .filter(nodeId -> nodeId != dongleNetworkInformation.getDongleNodeId())
                .forEach(nodeId -> {
                    reportsMap.getOrDefault(nodeId, new NodeReport()).dongleKnows = true;
                });
        List<NodeReport> nodeReports = new ArrayList<>(reportsMap.values());
        nodeReports.sort(Comparator.comparingInt(report -> report.nodeId));

        StringBuffer summary = new StringBuffer("node | cache | dongle | ping\n");
        nodeReports.stream().forEach(
                report -> {
                    console.flushLine("Ping node " + report.nodeId);
                    report.pingSucceeded = report.dongleKnows && nodeInformationService.pingNode(report.nodeId);
                    summary.append(String.format("0x%02X | %s | %s | %s\n",
                            report.nodeId,
                            report.cacheKnows ? "true " : "false",
                            report.dongleKnows ? "true  " : "false ",
                            report.pingSucceeded
                    ));
                }
        );

        boolean consistent = nodeReports.stream()
                .allMatch(report -> report.dongleKnows && report.cacheKnows && report.pingSucceeded);
        summary.append("\n")
                .append(consistent ?
                        "Network seems to be consistent however there is always some coincidence possible" :
                        "NOTE! Network inconsistency detected")
                .append("\n");

        return "\n" + summary.toString();
    }

    @ShellMethodAvailability
    public Availability checkAvailability() {

        if (ShellScope.NETWORK != shellContext.getScopeContext().getScope()) {
            return Availability.unavailable("Command not available in current scope");
        }

        return shellContext.getDongleDevicePath() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }

    private class NodeReport {
        private int nodeId;
        private boolean dongleKnows;
        private boolean cacheKnows;
        private boolean pingSucceeded;
    }
}
