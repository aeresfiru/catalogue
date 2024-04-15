package com.aeresfiru.manager.client.exception;

import lombok.Getter;
import org.springframework.http.ProblemDetail;

@Getter
public class ClientServerErrorException extends RuntimeException {

    private final ProblemDetail problemDetail;

    public ClientServerErrorException(ProblemDetail problemDetail) {
        this.problemDetail = problemDetail;
    }
}
