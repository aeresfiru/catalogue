package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.service.FavouriteProductService;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/feedback-api/v1/favourite-products")
@RequiredArgsConstructor
@Slf4j
public class FavouriteProductController {

    private final FavouriteProductService favouriteProductService;

    @GetMapping
    public Mono<Page<FavouriteProduct>> findFavouriteProducts(
            Mono<JwtAuthenticationToken> principalMono,
            @RequestParam(name = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "size", defaultValue = "10") Integer pageSize) {
        return principalMono.flatMap(principal ->
                favouriteProductService.findFavouriteProducts(getUserId(principal), PageRequest.of(pageNumber, pageSize)));
    }

    @GetMapping("/by-product/{productId}")
    public Mono<FavouriteProduct> findFavouriteProductByProductId(
            Mono<JwtAuthenticationToken> principalMono,
            @PathVariable("productId") int productId) {
        return principalMono.flatMap(principal ->
                this.favouriteProductService.findFavouriteProductByProduct(productId, getUserId(principal))
                        .switchIfEmpty(Mono.error(new NoSuchElementException("feedback.products.errors.not_found"))));
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> addProductToFavourites(
            Mono<JwtAuthenticationToken> principalMono,
            @Valid @RequestBody Mono<CreateFavouriteProductRequest> request,
            UriComponentsBuilder builder) {
        return principalMono.flatMap(principal ->
                request.flatMap(req -> this.favouriteProductService.addProductToFavourites(req, getUserId(principal)))
                        .map(product -> ResponseEntity
                                .created(builder
                                        .replacePath("/feedback-api/v1/favourite-products/{id}")
                                        .build(product.getId()))
                                .body(product)));
    }

    @DeleteMapping("/by-product/{productId}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(
            Mono<JwtAuthenticationToken> principalMono,
            @PathVariable("productId") int productId) {
        return principalMono.flatMap(principal ->
                this.favouriteProductService.removeProductFromFavourites(productId, getUserId(principal))
                        .thenReturn(ResponseEntity.noContent().build()));
    }

    private static String getUserId(JwtAuthenticationToken principal) {
        return principal.getToken().getSubject();
    }
}
