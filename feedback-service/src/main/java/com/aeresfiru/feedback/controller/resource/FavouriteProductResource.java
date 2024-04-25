package com.aeresfiru.feedback.controller.resource;

import java.io.Serializable;

public record FavouriteProductResource(String id, int productId, String userId)
        implements Serializable {
}
