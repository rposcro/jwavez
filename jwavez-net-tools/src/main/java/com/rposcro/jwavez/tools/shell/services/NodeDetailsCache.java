package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.tools.shell.models.NodeDetails;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Service
@Scope(SCOPE_SINGLETON)
public class NodeDetailsCache {

    private Map<Integer, NodeDetails> nodeDetailsMap = new HashMap<>();

    public boolean isNodeKnown(int nodeId) {
        return nodeDetailsMap.containsKey(nodeId);
    }

    public NodeDetails getNodeDetails(int nodeId) {
        return nodeDetailsMap.get(nodeId);
    }

    public void cacheNodeDetails(NodeDetails nodeDetails) {
        this.nodeDetailsMap.put(nodeDetails.getNodeId(), nodeDetails);
    }
}
