package com.aeresfiru.catalogue.controller.resource;

import com.aeresfiru.catalogue.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductResourceAssembler {

    public ProductResource toResource(Product product) {
        return new ProductResource(product.getId(), product.getTitle(), product.getDetails());
    }
}
