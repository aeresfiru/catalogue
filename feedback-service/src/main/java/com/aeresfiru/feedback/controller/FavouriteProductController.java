package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.service.FavouriteProductService;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/feedback-api/v1/favourite-products")
@RequiredArgsConstructor
public class FavouriteProductController {

    private final FavouriteProductService favouriteProductService;

    @GetMapping
    public Mono<Page<FavouriteProduct>> findFavouriteProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Mono<JwtAuthenticationToken> jwtAuthTokenMono) {
        return jwtAuthTokenMono.map(this::extractUserId)
                .flatMap(userId -> favouriteProductService.findFavouriteProducts(userId, PageRequest.of(page, size)));
    }

    @GetMapping("/by-product/{productId}")
    public Mono<FavouriteProduct> findFavouriteProductByProductId(
            @PathVariable("productId") int productId,
            Mono<JwtAuthenticationToken> jwtAuthTokenMono) {
        return jwtAuthTokenMono.map(this::extractUserId)
                .flatMap(userId -> favouriteProductService.findFavouriteProductByProduct(productId, userId));
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> addProductToFavourites(
            @Valid @RequestBody Mono<CreateFavouriteProductRequest> requestMono,
            Mono<JwtAuthenticationToken> jwtAuthTokenMono,
            UriComponentsBuilder builder) {
        return Mono.zip(jwtAuthTokenMono.map(this::extractUserId), requestMono)
                .flatMap(tuple -> favouriteProductService.addProductToFavourites(tuple.getT2(), tuple.getT1()))
                .map(favouriteProduct -> ResponseEntity.created(builder
                                .replacePath("/feedback-api/v1/favourite-products/{id}")
                                .build(favouriteProduct.getId()))
                        .body(favouriteProduct)
                );
    }

    @DeleteMapping("/by-product/{productId}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(
            @PathVariable("productId") int productId,
            Mono<JwtAuthenticationToken> jwtAuthTokenMono) {
        return jwtAuthTokenMono.map(this::extractUserId)
                .flatMap(userId -> favouriteProductService.removeProductFromFavourites(productId, userId))
                .thenReturn(ResponseEntity.noContent().build());
    }

    private String extractUserId(JwtAuthenticationToken jwtAuthenticationToken) {
        return jwtAuthenticationToken.getToken().getSubject();
    }
}
