package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.repository.ProductReviewRepository;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    public Mono<ProductReview> createProductReview(CreateProductReviewRequest request) {
        return this.mapToReview(request).flatMap(this.productReviewRepository::save);
    }

    @Override
    public Flux<ProductReview> findAllProductReviews(int productId) {
        return this.productReviewRepository.findAllByProductId(productId);
    }

    private Mono<ProductReview> mapToReview(CreateProductReviewRequest request) {
        return Mono.just(new ProductReview(UUID.randomUUID(), request.productId(), request.rating(), request.review()));
    }
}
