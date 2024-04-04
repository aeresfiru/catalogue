package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.repository.FavouriteProductRepository;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
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
    public Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request, String userId) {
        return this.mapToFavouriteProduct(request, userId).flatMap(this.repository::save);
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId, String userId) {
        return this.repository.removeByProductIdAndUserId(productId, userId);
    }

    @Override
    public Flux<FavouriteProduct> getFavouriteProducts(String userId) {
        return this.repository.findAllByUserId(userId);
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProduct(Integer productId, String userId) {
        return this.repository.findByProductIdAndUserId(productId, userId);
    }

    private Mono<FavouriteProduct> mapToFavouriteProduct(CreateFavouriteProductRequest req, String userId) {
        return Mono.just(new FavouriteProduct(UUID.randomUUID(), req.productId(), userId));
    }
}
