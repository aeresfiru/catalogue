package com.aeresfiru.customer.client.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class BadRequestClientException extends RuntimeException {

    private final List<String> errors;

    public BadRequestClientException(Throwable cause, List<String> errors) {
        super(cause);
        this.errors = errors;
    }
}
