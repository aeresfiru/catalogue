package com.aeresfiru.catalogue.controller.resource;

import java.io.Serializable;

public record ProductResource(int id, String title, String details)
        implements Serializable {
}
