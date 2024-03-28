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
    public Mono<FavouriteProduct> addProductToFavourites(Mono<CreateFavouriteProductRequest> request) {
        return this.repository.save(mapToFavouriteProduct(request));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId) {
        return this.repository.removeByProductId(productId);
    }

    @Override
    public Flux<FavouriteProduct> getFavouriteProducts() {
        return this.repository.findAll();
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProduct(Integer productId) {
        return this.repository.findByProductId(productId);
    }

    private static Mono<FavouriteProduct> mapToFavouriteProduct(Mono<CreateFavouriteProductRequest> request) {
        return request.map(req -> new FavouriteProduct(UUID.randomUUID(), req.productId()));
    }
}
