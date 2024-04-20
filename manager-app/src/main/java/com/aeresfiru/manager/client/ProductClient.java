package com.aeresfiru.manager.client;

import com.aeresfiru.manager.client.payload.CreateProductRequest;
import com.aeresfiru.manager.client.payload.PageResponse;
import com.aeresfiru.manager.client.payload.Product;
import com.aeresfiru.manager.client.payload.UpdateProductRequest;

public interface ProductClient {

    PageResponse<Product> findAllProducts(String filter, int page, int size);

    Product createProduct(CreateProductRequest request);

    Product findProduct(Integer productId);

    Product updateProduct(UpdateProductRequest request, Integer productId);

    void deleteProduct(Integer productId);
}
