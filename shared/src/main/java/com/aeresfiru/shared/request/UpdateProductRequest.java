package com.aeresfiru.shared.request;

import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record UpdateProductRequest(
        @Size(min = 3, max = 50, message = "{catalogue.products.update.errors.title_size_is_invalid}")
        String title,
        @Size(max = 1000, message = "{catalogue.products.update.errors.details_size_are_invalid}")
        String details) implements Serializable {
}
