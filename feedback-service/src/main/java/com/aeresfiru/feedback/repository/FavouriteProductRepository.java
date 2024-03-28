package com.aeresfiru.feedback.repository;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductRepository {

    Mono<FavouriteProduct> save(Mono<FavouriteProduct> favouriteProduct);

    Mono<Void> removeByProductId(int productId);

    Mono<FavouriteProduct> findByProductId(int productId);

    Flux<FavouriteProduct> findAll();
}
