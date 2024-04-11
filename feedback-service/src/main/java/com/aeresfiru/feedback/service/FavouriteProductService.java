package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request, String userId);

    Mono<Void> removeProductFromFavourites(int productId, String userId);

    Mono<PageImpl<FavouriteProduct>> findFavouriteProducts(String userId, Pageable pageRequest);

    Mono<FavouriteProduct> findFavouriteProductByProduct(Integer productId, String userId);

    Mono<Long> countAll();
}
