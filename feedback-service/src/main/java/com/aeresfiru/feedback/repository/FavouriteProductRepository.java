package com.aeresfiru.feedback.repository;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FavouriteProductRepository extends ReactiveCrudRepository<FavouriteProduct, UUID> {

    Mono<Void> removeByProductId(int productId);

    Mono<FavouriteProduct> findByProductId(int productId);
}
