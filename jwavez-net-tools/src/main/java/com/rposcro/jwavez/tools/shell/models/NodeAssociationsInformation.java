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
    private final Map<Integer, List<Integer>> associationsMap;

    public NodeAssociationsInformation() {
        this.associationGroupsMetas = new ArrayList<>();
        this.associationsMap = new HashMap<>();
    }

    @JsonCreator
    public NodeAssociationsInformation(
            @JsonProperty("groupsMetas") List<AssociationGroupMeta> associationGroupsMetas,
            @JsonProperty("associations") Map<Integer, List<Integer>> associationsMap) {
        this();
        if (associationGroupsMetas != null) {
            this.associationGroupsMetas.addAll(associationGroupsMetas);
        }
        if (associationsMap != null) {
            this.associationsMap.putAll(associationsMap);
        }
    }

    public boolean isGroupDefined(int groupId) {
        return findGroupMeta(groupId) != null;
    }

    public List<AssociationGroupMeta> getAssociationGroupsMetas() {
        return Collections.unmodifiableList(associationGroupsMetas);
    }

    public Map<Integer, List<Integer>> getAssociations() {
        return Collections.unmodifiableMap(associationsMap);
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

    public List<Integer> findAssociations(int groupId) {
        return associationsMap.containsKey(groupId) ? Collections.unmodifiableList(associationsMap.get(groupId)) : null;
    }

    public List<Integer> addAssociation(int groupId, int nodeId) {
        List<Integer> associatedNodes = associationsMap.get(groupId);
        if (associatedNodes == null) {
            associatedNodes = new ArrayList<>();
            associationsMap.put(groupId, associatedNodes);
        }
        if (!associatedNodes.contains(nodeId)) {
            associatedNodes.add(nodeId);
        }
        return Collections.unmodifiableList(associatedNodes);
    }

    public boolean removeAssociation(int groupId, int nodeId) {
        List<Integer> associatedNodes = associationsMap.get(groupId);
        if (associatedNodes != null) {
            return associatedNodes.remove(new Integer(nodeId));
        }
        return false;
    }

    public List<Integer> setAllAssociations(int groupId, List<Integer> associatedNodes) {
        return associationsMap.put(groupId, associatedNodes);
    }

    public List<Integer> removeAllAssociations(int groupId) {
        return associationsMap.remove(groupId);
    }

    public void wipeOutAll() {
        associationGroupsMetas.clear();
        associationsMap.clear();
    }
}