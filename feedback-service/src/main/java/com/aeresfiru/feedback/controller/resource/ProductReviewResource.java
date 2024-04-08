package com.aeresfiru.feedback.controller.resource;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ProductReviewResource implements Serializable {

    private String id;
    private int productId;
    private int rating;
    private String review;
    private String userId;
}
