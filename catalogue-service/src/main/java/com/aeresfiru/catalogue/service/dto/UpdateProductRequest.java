package com.aeresfiru.catalogue.service.dto;

import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public record UpdateProductRequest(

        @Length(min = 3, max = 50, message = "{catalogue.products.update.errors.title_size_is_invalid}")
        String title,

        @Length(max = 1000, message = "{catalogue.products.update.errors.details_size_are_invalid}")
        String details) implements Serializable {
}