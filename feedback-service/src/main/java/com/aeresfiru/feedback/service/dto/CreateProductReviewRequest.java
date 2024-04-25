package com.aeresfiru.feedback.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public record CreateProductReviewRequest(

        @NotNull(message = "{feedback.product.reviews.create.productId_is_null}")
        Integer productId,

        @NotNull(message = "{feedback.product.reviews.create.rating_is_null}")
        @Min(value = 0, message = "{feedback.product.reviews.create.rating_is_below_min}")
        @Max(value = 5, message = "{feedback.product.reviews.create.rating_is_above_max}")
        Integer rating,

        @Length(max = 1000, message = "{feedback.product.reviews.create.review_is_too_long}")
        String review) implements Serializable {
}
