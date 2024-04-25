package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.repository.ProductReviewRepository;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import com.aeresfiru.feedback.service.mapper.ProductReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductReviewMapper mapper;

    @Override
    public Mono<ProductReview> createProductReview(CreateProductReviewRequest request, String userId) {
        var review = this.mapper.mapToReview(request, userId);
        return this.productReviewRepository.save(review)
                .doOnSuccess((r) -> log.info("Product review created: {}", r))
                .doOnError((ex) -> log.error("Failed to create product review: {}", request, ex));
    }

    @Override
    public Mono<Page<ProductReview>> findAllProductReviews(int productId, PageRequest pageable) {
        return this.productReviewRepository.findAllByProductId(productId, pageable)
                .collectList()
                .zipWith(this.productReviewRepository.countByProductId(productId))
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }
}
