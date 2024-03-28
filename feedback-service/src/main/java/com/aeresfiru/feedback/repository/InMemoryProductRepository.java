package com.aeresfiru.feedback.repository;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Slf4j
public class InMemoryProductRepository implements FavouriteProductRepository {

    private final List<FavouriteProduct> favouriteProducts = new CopyOnWriteArrayList<>();

    @Override
    public Mono<FavouriteProduct> save(Mono<FavouriteProduct> favouriteProduct) {
        return favouriteProduct.doOnNext(this.favouriteProducts::add);
    }

    @Override
    public Mono<Void> removeByProductId(int productId) {
        return Mono.fromRunnable(() -> favouriteProducts.removeIf(product -> product.getProductId() == productId));
    }

    @Override
    public Mono<FavouriteProduct> findByProductId(int productId) {
        return Flux.fromIterable(favouriteProducts)
                .filter(product -> product.getProductId() == productId)
                .singleOrEmpty();
    }

    @Override
    public Flux<FavouriteProduct> findAll() {
        return Flux.fromIterable(this.favouriteProducts);
    }
}
