package com.aeresfiru.customer.service;

import com.aeresfiru.customer.client.payload.PageResponse;
import com.aeresfiru.customer.client.payload.Product;
import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<PageResponse<Product>> findAllProducts(String filter, Integer page, Integer size);

    Mono<Product> findProduct(Integer productId);
}
