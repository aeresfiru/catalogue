package com.aeresfiru.customer.service;

import com.aeresfiru.customer.client.payload.CreateFavouriteProductRequest;
import com.aeresfiru.customer.entity.FavouriteProduct;
import com.aeresfiru.customer.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<Void> removeProductFromFavourites(Integer productId);

    Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request);

    Mono<Boolean> isProductInFavourites(Integer productId);

    Flux<Product> findAllFavouriteProducts(String filter, int page, int pageSize);
}
