package com.aeresfiru.customer.entity;

import java.io.Serializable;

public record FavouriteProduct(String id, int productId) implements Serializable {
}
