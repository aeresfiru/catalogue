package com.aeresfiru.feedback.controller.resource;

import java.io.Serializable;

public record ProductReviewResource(String id, int productId, int rating, String review, String userId)
        implements Serializable {
}
