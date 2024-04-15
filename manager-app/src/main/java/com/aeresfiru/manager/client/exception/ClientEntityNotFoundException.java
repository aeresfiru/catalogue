package com.aeresfiru.manager.client.exception;

import lombok.Getter;
import org.springframework.http.ProblemDetail;

@Getter
public class ClientEntityNotFoundException extends RuntimeException {

    private final ProblemDetail problemDetail;

    public ClientEntityNotFoundException(ProblemDetail problemDetail) {
        this.problemDetail = problemDetail;
    }
}
