package com.aeresfiru.customer.client.payload;

import java.io.Serializable;

public record Product(int id, String title, String details) implements Serializable {
}
