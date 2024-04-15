package com.aeresfiru.customer.client.exception;

import java.util.List;

public class ClientBadRequestException extends RuntimeException {

    private final List<String> errors;

    public ClientBadRequestException(List<String> errors) {
        this.errors = errors;
    }

    public ClientBadRequestException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
