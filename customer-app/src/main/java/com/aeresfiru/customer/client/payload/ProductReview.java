package com.aeresfiru.customer.client.payload;

import java.io.Serializable;

public record ProductReview(String id, int productId, int rating, String review) implements Serializable {
}
