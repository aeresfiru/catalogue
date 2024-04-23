package com.aeresfiru.catalogue.service.mapper;

import com.aeresfiru.catalogue.entity.Product;
import com.aeresfiru.catalogue.service.dto.CreateProductRequest;
import com.aeresfiru.catalogue.service.dto.UpdateProductRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class ProductMapper {

    public Product mapToProduct(CreateProductRequest request) {
        return new Product(null, request.title(), request.details());
    }

    public void updateProduct(Product product, UpdateProductRequest request) {
        if (StringUtils.hasText(request.title())) {
            log.debug("New title provided in request: {}", request.title());
            product.setTitle(request.title());
        }
        if (StringUtils.hasText(request.details())) {
            log.debug("New details provided in request: {}", request.details());
            product.setDetails(request.details());
        }
    }
}
