package com.aeresfiru.feedback.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductReviewRequest(
        @NotNull(message = "{feedback.product.reviews.create.productId_is_null}")
        Integer productId,
        @NotNull(message = "{feedback.product.reviews.create.rating_is_null}")
        @Min(value = 1, message = "{feedback.product.reviews.create.rating_is_below_min}")
        @Max(value = 5, message = "{feedback.product.reviews.create.rating_is_above_max}")
        Integer rating,
        @Size(max = 1000, message = "{feedback.product.reviews.create.review_is_too_long}")
        String review) {
}
