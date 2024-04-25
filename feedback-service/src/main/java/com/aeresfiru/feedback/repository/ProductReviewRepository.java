package com.aeresfiru.feedback.repository;

import com.aeresfiru.feedback.entity.ProductReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductReviewRepository extends ReactiveCrudRepository<ProductReview, UUID> {

    Flux<ProductReview> findAllByProductId(int productId, Pageable pageable);

    Mono<Long> countByProductId(int productId);
}
