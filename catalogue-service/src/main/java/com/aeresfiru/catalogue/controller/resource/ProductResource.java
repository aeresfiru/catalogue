package com.aeresfiru.catalogue.controller.resource;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductResource(int id, String title, String details) implements Serializable {
}
