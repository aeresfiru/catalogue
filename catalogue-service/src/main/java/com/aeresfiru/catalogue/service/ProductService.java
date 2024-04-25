package com.aeresfiru.catalogue.service;

import com.aeresfiru.catalogue.entity.Product;
import com.aeresfiru.catalogue.service.dto.CreateProductRequest;
import com.aeresfiru.catalogue.service.dto.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<Product> findAllProducts(String filter, Pageable pageable);

    Product createProduct(CreateProductRequest request);

    Product findProduct(Integer productId);

    Product updateProduct(UpdateProductRequest request, Integer productId);

    void deleteProduct(Integer productId);
}
