package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.controller.resource.ProductReviewResource;
import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.service.ProductReviewService;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/feedback-api/v1/product-reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @GetMapping("/by-product/{productId}")
    public Flux<ProductReviewResource> findProductReviews(@PathVariable("productId") int productId) {
        return this.productReviewService.findAllProductReviews(productId)
                .flatMap(this::mapToProductReviewResource);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReviewResource>> createProductReview(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @Valid @RequestBody Mono<CreateProductReviewRequest> request,
            UriComponentsBuilder builder) {
        return Mono.zip(authenticationTokenMono, request)
                .flatMap(tuple ->
                        this.productReviewService.createProductReview(tuple.getT2(), getUserId(tuple.getT1())))
                .flatMap(this::mapToProductReviewResource)
                .map(review -> ResponseEntity
                        .created(builder
                                .replacePath("/feedback-api/v1/product-reviews/{reviewId}")
                                .build(review.getId()))
                        .body(review));
    }

    private static String getUserId(JwtAuthenticationToken token) {
        return token.getToken().getSubject();
    }

    private Mono<ProductReviewResource> mapToProductReviewResource(ProductReview review) {
        return Mono.just(new ProductReviewResource(review.getId().toString(), review.getProductId(),
                review.getRating(), review.getReview()));
    }
}
