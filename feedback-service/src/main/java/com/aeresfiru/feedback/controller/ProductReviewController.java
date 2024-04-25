package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.controller.resource.ProductReviewResource;
import com.aeresfiru.feedback.controller.resource.ProductReviewResourceAssembler;
import com.aeresfiru.feedback.service.ProductReviewService;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
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
@RequestMapping("/feedback-api/v1/product-reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;
    private final ProductReviewResourceAssembler resourceAssembler;

    @GetMapping
    public Mono<Page<ProductReviewResource>> findProductReviews(
            @RequestParam("productId") int productId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return this.productReviewService.findAllProductReviews(productId, PageRequest.of(page, size))
                .map(productReviews -> productReviews.map(this.resourceAssembler::toResource));
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
