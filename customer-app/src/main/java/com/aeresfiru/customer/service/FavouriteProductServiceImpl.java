package com.aeresfiru.customer.service;

import com.aeresfiru.customer.entity.FavouriteProduct;
import com.aeresfiru.customer.repository.FavouriteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FavouriteProductServiceImpl implements FavouriteProductService {

    private final FavouriteProductRepository repository;

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(int productId) {
        return this.repository.save(new FavouriteProduct(UUID.randomUUID(), productId));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId) {
        return this.repository.removeByProductId(productId);
    }

    @Override
    public Mono<Boolean> isFavouriteProduct(int productId) {
        return this.repository.findByProductId(productId).hasElement();
    }

    @Override
    public Flux<FavouriteProduct> getFavouriteProducts() {
        return this.repository.findAll();
    }
}
