package com.aeresfiru.customer.client;

import com.aeresfiru.customer.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductClient {

    Flux<Product> findAllProducts(String filter, Integer page, Integer size);

    Mono<Product> findProduct(Integer productId);
}
