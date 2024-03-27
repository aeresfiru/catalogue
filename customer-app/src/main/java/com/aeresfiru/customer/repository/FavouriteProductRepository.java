package com.aeresfiru.customer.repository;

import com.aeresfiru.customer.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductRepository {

    Mono<FavouriteProduct> save(FavouriteProduct favouriteProduct);

    Mono<Void> removeByProductId(int productId);

    Mono<FavouriteProduct> findByProductId(int productId);

    Flux<FavouriteProduct> findAll();
}
