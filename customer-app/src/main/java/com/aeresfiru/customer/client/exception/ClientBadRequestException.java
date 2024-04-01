package com.aeresfiru.customer.client.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class ClientBadRequestException extends RuntimeException {

    private final List<String> errors;

    public ClientBadRequestException(List<String> errors) {
        this.errors = errors;
    }

    public ClientBadRequestException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public ClientBadRequestException(String message, Throwable cause, List<String> errors) {
        super(message, cause);
        this.errors = errors;
    }

    public ClientBadRequestException(Throwable cause, List<String> errors) {
        super(cause);
        this.errors = errors;
    }
}
