package com.rposcro.jwavez.tools.shell.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeAssociationsInformation {

    private final List<AssociationGroupMeta> associationGroupsMetas;
    private final Map<Integer, List<Integer>> associatedNodesMap;
    private final Map<Integer, List<EndPointAddress>> associatedEndPointsMap;

    public NodeAssociationsInformation() {
        this.associationGroupsMetas = new ArrayList<>();
        this.associatedNodesMap = new HashMap<>();
        this.associatedEndPointsMap = new HashMap<>();
    }

    @JsonCreator
    public NodeAssociationsInformation(
            @JsonProperty("groupsMetas") List<AssociationGroupMeta> associationGroupsMetas,
            @JsonProperty("associatedNodes") Map<Integer, List<Integer>> associatedNodesMap,
            @JsonProperty("associatedEndPoints") Map<Integer, List<EndPointAddress>> associatedEndPointsMap) {
        this();
        if (associationGroupsMetas != null) {
            this.associationGroupsMetas.addAll(associationGroupsMetas);
        }
        if (associatedNodesMap != null) {
            this.associatedNodesMap.putAll(associatedNodesMap);
        }
        if (associatedEndPointsMap != null) {
            this.associatedEndPointsMap.putAll(associatedEndPointsMap);
        }
    }

    public boolean isGroupDefined(int groupId) {
        return findGroupMeta(groupId) != null;
    }

    public List<AssociationGroupMeta> getAssociationGroupsMetas() {
        return Collections.unmodifiableList(associationGroupsMetas);
    }

    public Map<Integer, List<Integer>> getAssociatedNodes() {
        return Collections.unmodifiableMap(associatedNodesMap);
    }

    public Map<Integer, List<EndPointAddress>> getAssociatedEndPoints() {
        return Collections.unmodifiableMap(associatedEndPointsMap);
    }

    public AssociationGroupMeta findGroupMeta(int groupId) {
        AssociationGroupMeta groupMeta = associationGroupsMetas.stream()
                .filter(meta -> meta.getGroupId() == groupId)
                .findFirst()
                .orElse(null);
        return groupMeta;
    }

    public AssociationGroupMeta removeGroupMeta(int groupId) {
        AssociationGroupMeta groupMeta = findGroupMeta(groupId);
        associationGroupsMetas.remove(groupMeta);
        return groupMeta;
    }

    public AssociationGroupMeta addOrReplaceGroupMeta(AssociationGroupMeta groupMeta) {
        AssociationGroupMeta existingGroupMeta = findGroupMeta(groupMeta.getGroupId());
        if (existingGroupMeta != null) {
            associationGroupsMetas.remove(existingGroupMeta);
        }
        associationGroupsMetas.add(groupMeta);
        Collections.sort(associationGroupsMetas, Comparator.comparingInt(AssociationGroupMeta::getGroupId));
        return existingGroupMeta;
    }

    public List<EndPointAddress> findEndPointAssociations(int groupId) {
        return associatedEndPointsMap.containsKey(groupId) ? Collections.unmodifiableList(associatedEndPointsMap.get(groupId)) : Collections.emptyList();
    }

    public List<EndPointAddress> addEndPointAssociation(int groupId, EndPointAddress endPointAddress) {
        List<EndPointAddress> associatedEndPoints = associatedEndPointsMap.get(groupId);
        if (associatedEndPoints == null) {
            associatedEndPoints = new ArrayList<>();
            associatedEndPointsMap.put(groupId, associatedEndPoints);
        }
        if (!associatedEndPoints.contains(endPointAddress)) {
            associatedEndPoints.add(endPointAddress);
        }
        return Collections.unmodifiableList(associatedEndPoints);
    }

    public boolean removeEndPointAssociation(int groupId, EndPointAddress endPointAddress) {
        List<EndPointAddress> associatedEndPoints = associatedEndPointsMap.get(groupId);
        if (associatedEndPoints != null) {
            return associatedEndPoints.remove(endPointAddress);
        }
        return false;
    }

    public List<EndPointAddress> replaceAllEndPointsAssociations(int groupId, List<EndPointAddress> associations) {
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
            return associatedNodes.remove(new Integer(nodeId));
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
        associationGroupsMetas.clear();
        associatedNodesMap.clear();
        associatedEndPointsMap.clear();
    }
}