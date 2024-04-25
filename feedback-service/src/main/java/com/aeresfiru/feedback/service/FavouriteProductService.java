package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request, String userId);

    Flux<FavouriteProduct> findFavouriteProducts(String userId);

    Mono<FavouriteProduct> findFavouriteProductByProduct(int productId, String userId);

    Mono<Void> removeProductFromFavourites(int productId, String userId);
}
