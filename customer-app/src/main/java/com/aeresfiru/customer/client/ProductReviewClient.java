package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.payload.CreateProductReviewRequest;
import com.aeresfiru.customer.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewClient {

    Flux<ProductReview> findProductReviewsByProductId(Integer productId);

    Mono<ProductReview> createProductReview(CreateProductReviewRequest request);
}
