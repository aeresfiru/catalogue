package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.controller.resource.FavouriteProductResource;
import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.service.FavouriteProductService;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/feedback-api/v1/favourite-products")
@RequiredArgsConstructor
public class FavouriteProductController {

    private final FavouriteProductService favouriteProductService;

    @GetMapping
    public Flux<FavouriteProductResource> findFavouriteProducts() {
        return this.favouriteProductService.getFavouriteProducts()
                .flatMap(this::mapToFavouriteProductResource);
    }

    @GetMapping("/by-product/{productId}")
    public Mono<FavouriteProductResource> findFavouriteProductByProductId(
            @PathVariable("productId") int productId) {
        return this.favouriteProductService.findFavouriteProductByProduct(productId)
                .flatMap(this::mapToFavouriteProductResource)
                .switchIfEmpty(Mono.error(new NoSuchElementException("feedback.products.errors.not_found")));
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProductResource>> addProductToFavourites(
            @Valid @RequestBody Mono<CreateFavouriteProductRequest> request,
            UriComponentsBuilder builder) {
        return this.favouriteProductService.addProductToFavourites(request)
                .flatMap(this::mapToFavouriteProductResource)
                .map(product -> ResponseEntity
                        .created(builder
                                .replacePath("/feedback-api/v1/favourite-products/{id}")
                                .build(product.getId()))
                        .body(product));
    }

    @DeleteMapping("/by-product/{productId}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(@PathVariable("productId") int productId) {
        return this.favouriteProductService.removeProductFromFavourites(productId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    private Mono<FavouriteProductResource> mapToFavouriteProductResource(FavouriteProduct product) {
        return Mono.just(new FavouriteProductResource(product.getId().toString(), product.getProductId()));
    }
}
