package com.jwavez.jwavez.products.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class Product {

    private int manufacturerId;
    private int productTypeId;
    private int productId;
    private String productName;
    private String productCode;
    private String manufacturerName;

    private List<Parameter> parameters = Collections.emptyList();
    private List<AssociationGroup> associationGroups = Collections.emptyList();

    public AssociationGroup findAssociationGroup(int associationGroupId) {
        return associationGroups.stream()
            .filter(associationGroup -> associationGroup.getGroupId() == associationGroupId)
            .findFirst()
            .orElse(null);
    }

    public Parameter findParameter(int parameterNumber) {
        return parameters.stream()
            .filter(parameter -> parameter.getNumber() == parameterNumber)
            .findFirst()
            .orElse(null);
    }

    public boolean hasAssociationGroup(int groupId) {
        return associationGroups.stream().anyMatch(group -> group.getGroupId() == groupId);
    }

    public boolean hasParameter(int parameterNumber) {
        return parameters.stream().anyMatch(parameter -> parameter.getNumber() == parameterNumber);
    }
}
