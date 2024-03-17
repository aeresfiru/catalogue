package com.aeresfiru.manager.client;

import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import org.springframework.http.ProblemDetail;

import java.util.List;

public interface ProductRestClient {

    List<Product> findAllProducts();

    Result<Product, ProblemDetail> createProduct(CreateProductRequest request);

    Result<Product, ProblemDetail> findProduct(Integer productId);

    Result<Product, ProblemDetail> updateProduct(UpdateProductRequest request, Integer productId);

    Result<Void, ProblemDetail> deleteProduct(Integer productId);
}
