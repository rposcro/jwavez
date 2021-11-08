package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.tools.shell.models.NodeDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Service
@Scope(SCOPE_SINGLETON)
public class NodeDetailsCache {

    @Autowired
    private CachePersistenceService cachePersistenceService;

    private Map<Integer, NodeDetails> nodeDetailsMap = new HashMap<>();

    @PostConstruct
    public void setupCache() {
        cachePersistenceService.restoreNodeDetails()
                .forEach(node -> nodeDetailsMap.put(node.getNodeId(), node));
    }

    public boolean isNodeKnown(int nodeId) {
        return nodeDetailsMap.containsKey(nodeId);
    }

    public NodeDetails getNodeDetails(int nodeId) {
        return nodeDetailsMap.get(nodeId);
    }

    public void cacheNodeDetails(NodeDetails nodeDetails) {
        this.nodeDetailsMap.put(nodeDetails.getNodeId(), nodeDetails);
        cachePersistenceService.persistNodesDetails(nodeDetailsMap.values());
    }

    public void persist() {
        cachePersistenceService.persistNodesDetails(nodeDetailsMap.values());
    }
}
