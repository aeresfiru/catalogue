package com.aeresfiru.customer.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductReviewRequest(
        @NotNull(message = "{customer.product.reviews.create.rating_is_null}")
        @Min(value = 1, message = "{customer.product.reviews.create.rating_is_below_min}")
        @Max(value = 5, message = "{customer.product.reviews.create.rating_is_above_max}")
        Integer rating,
        @Size(max = 1000, message = "{customer.product.reviews.create.review_is_too_long}")
        String review) {
}
