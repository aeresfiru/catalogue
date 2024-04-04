package com.aeresfiru.manager.client;

import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;

import java.util.List;

public interface ProductClient {

    List<Product> findAllProducts(String filter);

    Product createProduct(CreateProductRequest request);

    Product findProduct(Integer productId);

    Product updateProduct(UpdateProductRequest request, Integer productId);

    void deleteProduct(Integer productId);
}
