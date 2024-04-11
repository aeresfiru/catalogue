package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.service.ProductReviewService;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/feedback-api/v1/product-reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @GetMapping("/by-product/{productId}")
    public Flux<ProductReview> findProductReviews(@PathVariable("productId") int productId) {
        return this.productReviewService.findAllProductReviews(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @Valid @RequestBody Mono<CreateProductReviewRequest> request,
            UriComponentsBuilder builder) {
        return Mono.zip(authenticationTokenMono, request)
                .flatMap(tuple ->
                        this.productReviewService.createProductReview(tuple.getT2(), getUserId(tuple.getT1())))
                .map(review -> ResponseEntity
                        .created(builder
                                .replacePath("/feedback-api/v1/product-reviews/{reviewId}")
                                .build(review.getId()))
                        .body(review));
    }

    private static String getUserId(JwtAuthenticationToken token) {
        return token.getToken().getSubject();
    }
}
