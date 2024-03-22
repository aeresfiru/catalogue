package com.aeresfiru.manager.entity;

import java.io.Serializable;

public record Product(int id, String title, String details) implements Serializable {
}
