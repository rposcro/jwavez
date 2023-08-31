package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.tools.shell.models.AssociationGroupMeta;
import com.rposcro.jwavez.tools.shell.models.NodeAssociationsInformation;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeAssociationDefinitionService {

    @Autowired
    private NodeInformationCache nodeInformationCache;

    public void updateOrCreateMeta(int nodeId, int groupId, String memo) {
        AssociationGroupMeta groupMeta = AssociationGroupMeta.builder()
                .groupId(groupId)
                .memo(memo)
                .build();
        nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation().addOrReplaceGroupMeta(groupMeta);
    }

    public AssociationGroupMeta removeGroup(int nodeId, int groupId) {
        NodeAssociationsInformation associationsInformation = nodeInformationCache.getNodeDetails(nodeId).getAssociationsInformation();
        AssociationGroupMeta associationGroupMeta = associationsInformation.removeGroupMeta(groupId);
        associationsInformation.removeAllNodesAssociations(groupId);
        return associationGroupMeta;
    }

    public void cloneGroupMetas(int sourceNodeId, int targetNodeId) {
        NodeInformation sourceNode = nodeInformationCache.getNodeDetails(sourceNodeId);
        NodeInformation targetNode = nodeInformationCache.getNodeDetails(targetNodeId);
        targetNode.getAssociationsInformation().wipeOutAll();

        sourceNode.getAssociationsInformation().getAssociationGroupsMetas().stream().forEach(meta -> {
            targetNode.getAssociationsInformation().addOrReplaceGroupMeta(meta);
        });
    }
}
