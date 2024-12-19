package com.rposcro.jwavez.tools.shell.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwavez.jwavez.products.model.Product;
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
    public ProductsSpecificationsProvider(
        @Value("${jwz-shell.products.path}") String productsPath,
        ObjectMapper objectMapper) throws IOException {
        File productsFile = new File(productsPath);
        Product[] products = objectMapper.readValue(productsFile, Product[].class);
        this.productsMap = Stream.of(products)
            .collect(Collectors.toMap(this::productKey, Function.identity()));
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
