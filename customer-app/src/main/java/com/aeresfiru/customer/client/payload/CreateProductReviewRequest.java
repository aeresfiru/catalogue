package com.aeresfiru.customer.client.payload;

public record CreateProductReviewRequest(Integer productId, Integer rating, String review) {
}
