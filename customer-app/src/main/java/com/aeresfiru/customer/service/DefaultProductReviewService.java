package com.aeresfiru.customer.service;

import com.aeresfiru.customer.client.ProductReviewClient;
import com.aeresfiru.customer.client.payload.CreateProductReviewRequest;
import com.aeresfiru.customer.client.payload.ProductReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultProductReviewService implements ProductReviewService {

    private final ProductReviewClient productReviewClient;

    @Override
    public Flux<ProductReview> findProductReviews(Integer productId) {
        return this.productReviewClient.findProductReviewsByProductId(productId)
                .doOnError(ex -> log.error("Error retrieving product reviews with ID: {}", productId, ex));
    }

    @Override
    public Mono<ProductReview> createProductReview(CreateProductReviewRequest request) {
        return this.productReviewClient.createProductReview(request)
                .doOnError(ex -> log.error("Error creating product review", ex));
    }
}
