package com.aeresfiru.customer.client.exception;

public class ClientEntityNotFoundException extends RuntimeException {

    public ClientEntityNotFoundException() {
    }

    public ClientEntityNotFoundException(String message) {
        super(message);
    }

    public ClientEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientEntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
