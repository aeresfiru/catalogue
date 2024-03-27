package com.aeresfiru.customer.service;

import com.aeresfiru.customer.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<FavouriteProduct> addProductToFavourites(int productId);

    Mono<Void> removeProductFromFavourites(int productId);

    Mono<Boolean> isFavouriteProduct(int productId);

    Flux<FavouriteProduct> getFavouriteProducts();
}
