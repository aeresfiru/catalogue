package com.aeresfiru.customer.client.exception;

import lombok.Getter;
import org.springframework.http.ProblemDetail;

@Getter
public class ClientEntityNotFoundException extends RuntimeException {

    private final ProblemDetail problemDetail;

    public ClientEntityNotFoundException(ProblemDetail problemDetail) {
        this.problemDetail = problemDetail;
    }

    public ClientEntityNotFoundException(String message, ProblemDetail problemDetail) {
        super(message);
        this.problemDetail = problemDetail;
    }

    public ClientEntityNotFoundException(String message, Throwable cause, ProblemDetail problemDetail) {
        super(message, cause);
        this.problemDetail = problemDetail;
    }

    public ClientEntityNotFoundException(Throwable cause, ProblemDetail problemDetail) {
        super(cause);
        this.problemDetail = problemDetail;
    }
}
