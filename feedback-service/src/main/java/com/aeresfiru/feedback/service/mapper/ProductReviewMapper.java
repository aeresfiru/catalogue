package com.aeresfiru.feedback.service.mapper;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductReviewMapper {

    public ProductReview mapToReview(CreateProductReviewRequest request, String userId) {
        return new ProductReview(UUID.randomUUID(), request.productId(), request.rating(), request.review(), userId);
    }
}
