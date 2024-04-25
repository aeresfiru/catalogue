package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.repository.FavouriteProductRepository;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import com.aeresfiru.feedback.service.mapper.FavouriteProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavouriteProductServiceImpl implements FavouriteProductService {

    private final FavouriteProductRepository repository;
    private final FavouriteProductMapper mapper;

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request, String userId) {
        var favouriteProduct = this.mapper.mapToFavouriteProduct(request, userId);
        return this.repository.save(favouriteProduct)
                .doOnSuccess((product) -> log.info("Product '{}' added to favourites", product))
                .doOnError((ex) -> log.error("Failed to add product to favourites", ex));
    }

    @Override
    public Flux<FavouriteProduct> findFavouriteProducts(String userId) {
        return this.repository.findAllByUserId(userId)
                .doOnError(ex -> log.error("Failed to retrieve favourite products, user ID: '{}'", userId, ex));
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProduct(int productId, String userId) {
        return this.repository.findByProductIdAndUserId(productId, userId)
                .doOnError(ex -> log.error("Failed to retrieve favourite product, product ID: '{}'", productId, ex))
                .switchIfEmpty(Mono.error(new NoSuchElementException("feedback.products.errors.not_found")));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId, String userId) {
        return this.repository.deleteByProductIdAndUserId(productId, userId)
                .doOnSuccess((product) -> log.info("Product '{}' removed to favourites", product))
                .doOnError((ex) -> log.error("Failed to remove product from favourites", ex));
    }
}
