package com.aeresfiru.manager.client.payload;

import java.io.Serializable;

public record UpdateProductRequest(String title, String details) implements Serializable {
}
