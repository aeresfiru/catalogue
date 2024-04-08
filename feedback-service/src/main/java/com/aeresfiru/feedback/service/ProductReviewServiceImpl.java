package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.repository.ProductReviewRepository;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    public Mono<ProductReview> createProductReview(CreateProductReviewRequest request, String userId) {
        return this.mapToReview(request, userId)
                .flatMap(this.productReviewRepository::save)
                .doOnNext((review) -> log.info("Product review created: {}", review));
    }

    @Override
    public Flux<ProductReview> findAllProductReviews(int productId) {
        return this.productReviewRepository.findAllByProductId(productId);
    }

    private Mono<ProductReview> mapToReview(CreateProductReviewRequest req, String userId) {
        return Mono.just(new ProductReview(UUID.randomUUID(), req.productId(), req.rating(), req.review(), userId));
    }
}
