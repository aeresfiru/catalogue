package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.payload.CreateFavouriteProductRequest;
import com.aeresfiru.customer.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductClient {

    Flux<FavouriteProduct> findAllFavouriteProducts();

    Mono<FavouriteProduct> findFavouriteProductByProductId(Integer productId);

    Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request);

    Mono<Void> removeProductFromFavourites(Integer productId);
}
