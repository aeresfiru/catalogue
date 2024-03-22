package com.aeresfiru.shared.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record CreateProductRequest(
        @NotBlank(message = "{catalogue.products.create.errors.title_is_blank}")
        @Size(min = 3, max = 50, message = "{catalogue.products.create.errors.title_size_is_invalid}")
        String title,
        @Size(max = 1000, message = "{catalogue.products.create.errors.details_size_are_invalid}")
        String details) implements Serializable {
}
