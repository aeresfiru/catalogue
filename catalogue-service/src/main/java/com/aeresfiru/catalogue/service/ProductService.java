package com.aeresfiru.catalogue.service;

import com.aeresfiru.catalogue.entity.Product;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;

import java.util.List;

public interface ProductService {

    List<Product> findAllProducts(String filter);

    Product createProduct(CreateProductRequest request);

    Product findProduct(Integer productId);

    Product updateProductPartially(UpdateProductRequest request, Integer productId);

    void deleteProduct(Integer productId);

    Product updateProduct(UpdateProductRequest request, Integer productId);
}
