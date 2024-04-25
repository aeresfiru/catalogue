package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

public interface ProductReviewService {

    Mono<ProductReview> createProductReview(CreateProductReviewRequest request, String userId);

    Mono<Page<ProductReview>> findAllProductReviews(int productId, PageRequest pageRequest);
}
