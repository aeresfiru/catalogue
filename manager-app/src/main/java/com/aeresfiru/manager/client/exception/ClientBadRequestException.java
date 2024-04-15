package com.aeresfiru.manager.client.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ClientBadRequestException extends RuntimeException {

    private final List<String> errors;

    public ClientBadRequestException(List<String> errors) {
        this.errors = errors;
    }
}
