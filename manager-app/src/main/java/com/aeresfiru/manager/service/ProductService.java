package com.aeresfiru.manager.service;

import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.manager.service.dto.CreateProductRequest;
import com.aeresfiru.manager.service.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {

    List<Product> findAllProducts();

    Product createProduct(CreateProductRequest request);

    Product findProduct(Integer productId);

    void updateProduct(UpdateProductRequest request, Integer productId);

    void delete(Product product);
}
