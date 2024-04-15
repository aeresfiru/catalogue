package com.aeresfiru.manager.client.payload;

import java.io.Serializable;

public record CreateProductRequest(String title, String details) implements Serializable {
}
