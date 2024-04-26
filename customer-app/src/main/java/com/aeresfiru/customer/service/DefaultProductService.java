package com.aeresfiru.customer.service;

import com.aeresfiru.customer.client.ProductClient;
import com.aeresfiru.customer.client.payload.PageResponse;
import com.aeresfiru.customer.client.payload.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultProductService implements ProductService {

    private final ProductClient productClient;

    @Override
    public Mono<PageResponse<Product>> findAllProducts(String filter, Integer pageNumber, Integer size) {
        return this.productClient.findAllProducts(filter, pageNumber, size)
                .doOnError(ex -> log.error("Error retrieving products. Filter : {}, page: {}, size: {}",
                        filter, pageNumber, size, ex));
    }

    @Override
    public Mono<Product> findProduct(Integer productId) {
        return this.productClient.findProduct(productId)
                .doOnError(ex -> log.error("Error retrieving product by ID: {}", productId, ex));
    }
}
