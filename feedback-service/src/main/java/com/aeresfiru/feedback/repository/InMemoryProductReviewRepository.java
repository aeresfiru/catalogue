package com.aeresfiru.feedback.repository;

import com.aeresfiru.feedback.entity.ProductReview;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryProductReviewRepository implements ProductReviewRepository {

    private final List<ProductReview> productReviews = new CopyOnWriteArrayList<>();

    @Override
    public Mono<ProductReview> save(ProductReview productReview) {
        return Mono.fromRunnable(() -> this.productReviews.add(productReview))
                .thenReturn(productReview);
    }

    @Override
    public Flux<ProductReview> findAllByProductId(int productId) {
        return Flux.fromIterable(this.productReviews)
                .filter(productReview -> productReview.getProductId() == productId);
    }
}
