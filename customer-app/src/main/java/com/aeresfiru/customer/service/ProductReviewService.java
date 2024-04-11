package com.aeresfiru.customer.service;

import com.aeresfiru.customer.client.payload.CreateProductReviewRequest;
import com.aeresfiru.customer.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewService {

    Flux<ProductReview> findProductReviews(Integer productId);

    Mono<ProductReview> createProductReview(CreateProductReviewRequest request);
}
