package com.aeresfiru.manager.client.exception;

import lombok.Getter;
import org.springframework.http.ProblemDetail;

@Getter
public class ClientBadRequestException extends RuntimeException {

    private ProblemDetail problemDetail;

    public ClientBadRequestException(ProblemDetail problemDetail) {
        this.problemDetail = problemDetail;
    }

    public ClientBadRequestException(String message, ProblemDetail problemDetail) {
        super(message);
        this.problemDetail = problemDetail;
    }

    public ClientBadRequestException(String message, Throwable cause, ProblemDetail problemDetail) {
        super(message, cause);
        this.problemDetail = problemDetail;
    }

    public ClientBadRequestException(Throwable cause, ProblemDetail problemDetail) {
        super(cause);
        this.problemDetail = problemDetail;
    }
}
