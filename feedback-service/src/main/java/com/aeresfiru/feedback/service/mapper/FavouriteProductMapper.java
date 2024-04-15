package com.aeresfiru.feedback.service.mapper;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FavouriteProductMapper {

    public FavouriteProduct mapToFavouriteProduct(CreateFavouriteProductRequest request, String userId) {
        return new FavouriteProduct(UUID.randomUUID(), request.productId(), userId);
    }
}
