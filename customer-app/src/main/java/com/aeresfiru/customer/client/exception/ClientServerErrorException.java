package com.aeresfiru.customer.client.exception;

import org.springframework.http.ProblemDetail;

public class ClientServerErrorException extends RuntimeException {

    private ProblemDetail problemDetail;

    public ClientServerErrorException(ProblemDetail problemDetail) {
        this.problemDetail = problemDetail;
    }

    public ClientServerErrorException(String message, ProblemDetail problemDetail) {
        super(message);
        this.problemDetail = problemDetail;
    }

    public ClientServerErrorException(String message, Throwable cause, ProblemDetail problemDetail) {
        super(message, cause);
        this.problemDetail = problemDetail;
    }

    public ClientServerErrorException(Throwable cause, ProblemDetail problemDetail) {
        super(cause);
        this.problemDetail = problemDetail;
    }

    public ProblemDetail getProblemDetail() {
        return problemDetail;
    }
}
