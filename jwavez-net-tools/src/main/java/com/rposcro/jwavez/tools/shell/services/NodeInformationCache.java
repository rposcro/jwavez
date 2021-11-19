package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Service
@Scope(SCOPE_SINGLETON)
public class NodeInformationCache {

    @Autowired
    private CachePersistenceService cachePersistenceService;

    private Map<Integer, NodeInformation> nodeInformationMap = new HashMap<>();

    @PostConstruct
    public void setupCache() {
        cachePersistenceService.restoreNodeDetails()
                .forEach(node -> nodeInformationMap.put(node.getNodeId(), node));
    }

    public boolean isNodeKnown(int nodeId) {
        return nodeInformationMap.containsKey(nodeId);
    }

    public List<NodeInformation> getOrderedNodeList() {
        return nodeInformationMap.values().stream()
                .sorted(Comparator.comparingInt(NodeInformation::getNodeId))
                .collect(Collectors.toList());
    }

    public NodeInformation getNodeDetails(int nodeId) {
        return nodeInformationMap.get(nodeId);
    }

    public void cacheNodeDetails(NodeInformation nodeDetails) {
        this.nodeInformationMap.put(nodeDetails.getNodeId(), nodeDetails);
    }

    public NodeInformation removeNodeDetails(int nodeId) {
        return this.nodeInformationMap.remove(nodeId);
    }

    public void persist() {
        cachePersistenceService.persistNodesDetails(nodeInformationMap.values());
    }
}
