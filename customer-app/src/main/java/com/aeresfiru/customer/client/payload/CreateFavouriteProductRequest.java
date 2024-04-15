package com.aeresfiru.customer.client.payload;

import java.io.Serializable;

public record CreateFavouriteProductRequest(Integer productId) implements Serializable {
}
