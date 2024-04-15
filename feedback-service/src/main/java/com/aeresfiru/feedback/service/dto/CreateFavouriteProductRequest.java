package com.aeresfiru.feedback.service.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record CreateFavouriteProductRequest(

        @NotNull(message = "{feedback.product.favourites.create.productId_is_null}")
        Integer productId) implements Serializable {
}
