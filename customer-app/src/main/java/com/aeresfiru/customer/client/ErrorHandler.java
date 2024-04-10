package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.exception.ClientBadRequestException;
import com.aeresfiru.customer.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.customer.client.exception.ClientServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class ErrorHandler {

    public Mono<? extends Throwable> handleClientError(Mono<ProblemDetail> errorResponse) {
        return errorResponse.flatMap(problemDetail -> {
            int status = problemDetail.getStatus();
            if (status == HttpStatus.NOT_FOUND.value()) {
                log.error("Req");
                return Mono.error(new ClientEntityNotFoundException(problemDetail));
            } else if (status == HttpStatus.BAD_REQUEST.value()) {
                return extractErrors(problemDetail)
                        .collectList()
                        .flatMap(errors -> Mono.error(new ClientBadRequestException(errors)));
            } else {
                log.error("Unexpected client error status: {}", status);
                return Mono.error(new ClientServerErrorException(problemDetail));
            }
        });
    }

    public Mono<? extends Throwable> handleServerError(Mono<ProblemDetail> errorResponse) {
        return errorResponse.flatMap(problemDetail -> {
            log.error("Server error occurred: {}", problemDetail);
            return Mono.error(new ClientServerErrorException(problemDetail));
        });
    }

    private Flux<String> extractErrors(ProblemDetail error) {
        if (error.getProperties() != null && error.getProperties().containsKey("errors")) {
            return Flux.fromIterable((List<?>) error.getProperties().get("errors"))
                    .filter(String.class::isInstance)
                    .map(String::valueOf);
        }
        log.error("Validation error occurred, but no error messages found: {}", error);
        return Flux.empty();
    }
}
