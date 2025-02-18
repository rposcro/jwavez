package com.rposcro.jwavez.tools.shell.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwavez.jwavez.products.model.AssociationGroup;
import com.jwavez.jwavez.products.model.Parameter;
import com.jwavez.jwavez.products.model.Product;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ProductsSpecificationsProvider {

    private final Map<ProductKey, Product> productsMap;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    public ProductsSpecificationsProvider(
        @Value("${jwz-shell.products.path}") String productsPath,
        ObjectMapper objectMapper) throws IOException {
        File productsFile = new File(productsPath);
        Product[] products = objectMapper.readValue(productsFile, Product[].class);
        this.productsMap = Stream.of(products)
            .collect(Collectors.toMap(this::productKey, Function.identity()));
    }

    public Product findProduct(int nodeId) {
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        return findProduct(nodeInformation);
    }

    public Product findProduct(NodeInformation nodeInformation) {
        Product product = productsMap.get(productKey(nodeInformation));
        if (product == null) {
            product = unknownProduct(nodeInformation);
        }
        return product;
    }

    public Parameter findParameter(int nodeId, int parameterNumber) {
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        return findParameter(nodeInformation, parameterNumber);
    }

    public Parameter findParameter(NodeInformation nodeInformation, int parameterNumber) {
        Product product = findProduct(nodeInformation);
        return product.findParameter(parameterNumber);
    }

    public int[] findAssociationsGroupsIds(int nodeId) {
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        Product product = findProduct(nodeInformation);
        return product.getAssociationGroups().stream()
            .mapToInt(AssociationGroup::getGroupId)
            .toArray();
    }

    public int[] findParameterNumbers(int nodeId) {
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        Product product = findProduct(nodeInformation);
        return product.getParameters().stream()
            .mapToInt(Parameter::getNumber)
            .toArray();
    }

    public boolean hasAssociationGroup(int nodeId, int groupId) {
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        Product product = findProduct(nodeInformation);
        return product.hasAssociationGroup(groupId);
    }

    public boolean hasParameter(int nodeId, int parameterNumber) {
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        Product product = findProduct(nodeInformation);
        return product.hasParameter(parameterNumber);
    }

    private Product unknownProduct(NodeInformation nodeInformation) {
        Product product = new Product();
        product.setManufacturerId(nodeInformation.getProductInformation().getManufacturerId());
        product.setProductId(nodeInformation.getProductInformation().getProductId());
        product.setProductTypeId(nodeInformation.getProductInformation().getProductTypeId());
        product.setProductName("Unknown product");
        return product;
    }

    private ProductKey productKey(NodeInformation nodeInformation) {
        return new ProductKey(
            nodeInformation.getProductInformation().getProductId(),
            nodeInformation.getProductInformation().getProductTypeId(),
            nodeInformation.getProductInformation().getManufacturerId()
        );
    }

    private ProductKey productKey(Product product) {
        return new ProductKey(product.getProductId(), product.getProductTypeId(), product.getManufacturerId());
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    private static class ProductKey {
        private int productId;
        private int productTypeId;
        private int manufacturerId;
    }
}
