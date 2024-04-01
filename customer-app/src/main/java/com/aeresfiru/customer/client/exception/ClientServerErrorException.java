package com.aeresfiru.customer.client.exception;

public class ClientServerErrorException extends RuntimeException {

    public ClientServerErrorException() {
    }

    public ClientServerErrorException(String message) {
        super(message);
    }

    public ClientServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientServerErrorException(Throwable cause) {
        super(cause);
    }
}
