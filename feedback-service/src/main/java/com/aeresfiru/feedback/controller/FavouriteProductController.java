package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.controller.resource.FavouriteProductResource;
import com.aeresfiru.feedback.controller.resource.FavouriteProductResourceAssembler;
import com.aeresfiru.feedback.service.FavouriteProductService;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/feedback-api/v1/favourite-products")
@RequiredArgsConstructor
public class FavouriteProductController {

    private final FavouriteProductService favouriteProductService;
    private final FavouriteProductResourceAssembler resourceAssembler;

    @GetMapping
    public Flux<FavouriteProductResource> findFavouriteProducts(
            Mono<JwtAuthenticationToken> jwtAuthTokenMono) {
        return jwtAuthTokenMono.flatMap(this::extractUserId)
                .flatMapMany(authToken -> favouriteProductService.findFavouriteProducts(authToken)
                        .map(this.resourceAssembler::toResource));
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProductResource>> addProductToFavourites(
            @Valid @RequestBody Mono<CreateFavouriteProductRequest> requestMono,
            Mono<JwtAuthenticationToken> jwtAuthTokenMono,
            UriComponentsBuilder builder) {
        return Mono.zip(jwtAuthTokenMono.flatMap(this::extractUserId), requestMono)
                .flatMap(tuple -> favouriteProductService.addProductToFavourites(tuple.getT2(), tuple.getT1()))
                .map(this.resourceAssembler::toResource)
                .map(favouriteProduct -> ResponseEntity.created(builder
                                .replacePath("/feedback-api/v1/favourite-products/{id}")
                                .build(favouriteProduct.id()))
                        .body(favouriteProduct)
                );
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(
            @RequestParam("productId") int productId,
            Mono<JwtAuthenticationToken> jwtAuthTokenMono) {
        return jwtAuthTokenMono.flatMap(this::extractUserId)
                .flatMap(userId -> favouriteProductService.removeProductFromFavourites(productId, userId))
                .thenReturn(ResponseEntity.noContent().build());
    }

    private Mono<String> extractUserId(JwtAuthenticationToken jwtAuthenticationToken) {
        return Mono.just(jwtAuthenticationToken.getToken().getSubject());
    }
}
