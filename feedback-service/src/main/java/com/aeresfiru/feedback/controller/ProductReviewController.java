package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.controller.resource.ProductReviewResource;
import com.aeresfiru.feedback.controller.resource.ProductReviewResourceAssembler;
import com.aeresfiru.feedback.service.ProductReviewService;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/feedback-api/v1/product-reviews")
@RequiredArgsConstructor
@Slf4j
public class ProductReviewController {

    private final ProductReviewService productReviewService;
    private final ProductReviewResourceAssembler resourceAssembler;

    @GetMapping
    public Flux<ProductReviewResource> findProductReviews(
            @RequestParam("productId") int productId
    ) {
        return this.productReviewService.findAllProductReviews(productId)
                .map(this.resourceAssembler::toResource);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReviewResource>> createProductReview(
            Mono<JwtAuthenticationToken> jwtAuthTokenMono,
            @Valid @RequestBody Mono<CreateProductReviewRequest> requestMono,
            UriComponentsBuilder builder) {
        return Mono.zip(jwtAuthTokenMono.map(token -> token.getToken().getSubject()), requestMono)
                .flatMap(t -> this.productReviewService.createProductReview(t.getT2(), t.getT1()))
                .map(this.resourceAssembler::toResource)
                .map(review -> ResponseEntity
                        .created(builder
                                .replacePath("/feedback-api/v1/product-reviews/{reviewId}")
                                .build(review.id()))
                        .body(review));
    }
}
