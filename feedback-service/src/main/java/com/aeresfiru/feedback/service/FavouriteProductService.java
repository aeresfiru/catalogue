package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<FavouriteProduct> addProductToFavourites(Mono<CreateFavouriteProductRequest> request);

    Mono<Void> removeProductFromFavourites(int productId);

    Flux<FavouriteProduct> getFavouriteProducts();

    Mono<FavouriteProduct> findFavouriteProductByProduct(Integer productId);
}
