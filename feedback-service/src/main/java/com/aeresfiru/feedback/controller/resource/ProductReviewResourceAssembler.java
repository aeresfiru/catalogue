package com.aeresfiru.feedback.controller.resource;

import com.aeresfiru.feedback.entity.ProductReview;
import org.springframework.stereotype.Component;

@Component
public class ProductReviewResourceAssembler {

    public ProductReviewResource toResource(ProductReview review) {
        return new ProductReviewResource(review.getId().toString(), review.getProductId(),
                review.getRating(), review.getReview(), review.getUserId());
    }
}
