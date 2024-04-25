package com.aeresfiru.feedback.controller.resource;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import org.springframework.stereotype.Component;

@Component
public class FavouriteProductResourceAssembler {

    public FavouriteProductResource toResource(FavouriteProduct product) {
        return new FavouriteProductResource(product.getId().toString(), product.getProductId(), product.getUserId());
    }
}
