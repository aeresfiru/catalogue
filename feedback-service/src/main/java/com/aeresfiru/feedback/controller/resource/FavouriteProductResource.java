package com.aeresfiru.feedback.controller.resource;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class FavouriteProductResource implements Serializable {

    private String id;
    private int productId;
    private String userId;
}
