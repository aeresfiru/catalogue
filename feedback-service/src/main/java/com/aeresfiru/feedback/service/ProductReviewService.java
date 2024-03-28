package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewService {

    Mono<ProductReview> createProductReview(CreateProductReviewRequest request);

    Flux<ProductReview> findAllProductReviews(int productId);
}
