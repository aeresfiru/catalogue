package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request, String userId);

    Mono<Page<FavouriteProduct>> findFavouriteProducts(String userId, Pageable pageable);

    Mono<FavouriteProduct> findFavouriteProductByProduct(int productId, String userId);

    Mono<Void> removeProductFromFavourites(int productId, String userId);
}
