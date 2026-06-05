package com.rposcro.jwavez.tools.shell.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeAssociationsInformation {

    private final Map<Integer, List<Integer>> associatedNodesMap;
    private final Map<Integer, List<EndPointMark>> associatedEndPointsMap;

    public NodeAssociationsInformation() {
        this.associatedNodesMap = new HashMap<>();
        this.associatedEndPointsMap = new HashMap<>();
    }

    @JsonCreator
    public NodeAssociationsInformation(
            @JsonProperty("associatedNodes") Map<Integer, List<Integer>> associatedNodesMap,
            @JsonProperty("associatedEndPoints") Map<Integer, List<EndPointMark>> associatedEndPointsMap) {
        this();
        if (associatedNodesMap != null) {
            this.associatedNodesMap.putAll(associatedNodesMap);
        }
        if (associatedEndPointsMap != null) {
            this.associatedEndPointsMap.putAll(associatedEndPointsMap);
        }
    }

    public Map<Integer, List<Integer>> getAssociatedNodes() {
        return Collections.unmodifiableMap(associatedNodesMap);
    }

    public Map<Integer, List<EndPointMark>> getAssociatedEndPoints() {
        return Collections.unmodifiableMap(associatedEndPointsMap);
    }

    public List<EndPointMark> findEndPointAssociations(int groupId) {
        return associatedEndPointsMap.containsKey(groupId) ? Collections.unmodifiableList(associatedEndPointsMap.get(groupId)) : Collections.emptyList();
    }

    public List<EndPointMark> addEndPointAssociation(int groupId, EndPointMark endPointMark) {
        List<EndPointMark> associatedEndPoints = associatedEndPointsMap.get(groupId);
        if (associatedEndPoints == null) {
            associatedEndPoints = new ArrayList<>();
            associatedEndPointsMap.put(groupId, associatedEndPoints);
        }
        if (!associatedEndPoints.contains(endPointMark)) {
            associatedEndPoints.add(endPointMark);
        }
        return Collections.unmodifiableList(associatedEndPoints);
    }

    public boolean removeEndPointAssociation(int groupId, EndPointMark endPointMark) {
        List<EndPointMark> associatedEndPoints = associatedEndPointsMap.get(groupId);
        if (associatedEndPoints != null) {
            return associatedEndPoints.remove(endPointMark);
        }
        return false;
    }

    public List<EndPointMark> replaceAllEndPointsAssociations(int groupId, List<EndPointMark> associations) {
        return associatedEndPointsMap.put(groupId, associations);
    }

    public List<Integer> findNodeAssociations(int groupId) {
        return associatedNodesMap.containsKey(groupId) ? Collections.unmodifiableList(associatedNodesMap.get(groupId)) : Collections.emptyList();
    }

    public List<Integer> addNodeAssociation(int groupId, int nodeId) {
        List<Integer> associatedNodes = associatedNodesMap.get(groupId);
        if (associatedNodes == null) {
            associatedNodes = new ArrayList<>();
            associatedNodesMap.put(groupId, associatedNodes);
        }
        if (!associatedNodes.contains(nodeId)) {
            associatedNodes.add(nodeId);
        }
        return Collections.unmodifiableList(associatedNodes);
    }

    public boolean removeNodeAssociation(int groupId, int nodeId) {
        List<Integer> associatedNodes = associatedNodesMap.get(groupId);
        if (associatedNodes != null) {
            return associatedNodes.remove(Integer.valueOf(nodeId));
        }
        return false;
    }

    public List<Integer> replaceAllNodesAssociations(int groupId, List<Integer> associatedNodes) {
        return associatedNodesMap.put(groupId, associatedNodes);
    }

    public List<Integer> removeAllNodesAssociations(int groupId) {
        return associatedNodesMap.remove(groupId);
    }

    public void wipeOutAll() {
        associatedNodesMap.clear();
        associatedEndPointsMap.clear();
    }
}
