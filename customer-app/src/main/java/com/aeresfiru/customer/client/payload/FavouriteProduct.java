package com.aeresfiru.customer.client.payload;

import java.io.Serializable;

public record FavouriteProduct(String id, int productId) implements Serializable {
}
