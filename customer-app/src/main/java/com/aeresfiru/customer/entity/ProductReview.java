package com.aeresfiru.customer.entity;

import java.io.Serializable;

public record ProductReview(String id, int productId, int rating, String review) implements Serializable {
}
