package com.aeresfiru.catalogue.service.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public record CreateProductRequest(

        @NotBlank(message = "{catalogue.products.create.errors.title_is_blank}")
        @Length(min = 3, max = 50, message = "{catalogue.products.create.errors.title_size_is_invalid}")
        String title,

        @Length(max = 1000, message = "{catalogue.products.create.errors.details_size_are_invalid}")
        String details) implements Serializable {
}
