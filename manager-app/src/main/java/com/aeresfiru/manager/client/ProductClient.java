package com.aeresfiru.manager.client;

import com.aeresfiru.manager.client.payload.CreateProductRequest;
import com.aeresfiru.manager.client.payload.Product;
import com.aeresfiru.manager.client.payload.UpdateProductRequest;

import java.util.List;

public interface ProductClient {

    List<Product> findAllProducts(String filter);

    Product createProduct(CreateProductRequest request);

    Product findProduct(Integer productId);

    Product updateProduct(UpdateProductRequest request, Integer productId);

    void deleteProduct(Integer productId);
}
