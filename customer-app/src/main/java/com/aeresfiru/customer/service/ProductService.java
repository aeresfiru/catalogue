package com.aeresfiru.customer.service;

import com.aeresfiru.customer.entity.Product;
import com.aeresfiru.shared.client.PageApiResponse;
import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<PageApiResponse<Product>> findAllProducts(String filter, Integer page, Integer size);

    Mono<Product> findProduct(Integer productId);
}
