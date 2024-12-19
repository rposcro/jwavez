package com.jwavez.jwavez.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Product {

    private int manufacturerId;
    private int productTypeId;
    private int productId;
    private String productName;
    private String productCode;
    private String manufacturerName;

    private List<Parameter> parameters;
    private List<AssociationGroup> associationGroups;
}
