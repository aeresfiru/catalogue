package com.aeresfiru.manager.client;

import com.aeresfiru.manager.client.exception.ClientBadRequestException;
import com.aeresfiru.manager.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.manager.client.exception.ClientServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Component
@Slf4j
public class RequestExecutor {

    public <T> T execute(Supplier<T> requestSupplier) {
        try {
            return requestSupplier.get();
        } catch (HttpClientErrorException.NotFound ex) {
            var problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
            log.error("Request failed, server resource not found, details: {}", ex.getMessage());
            throw new ClientEntityNotFoundException(problemDetail);
        } catch (HttpClientErrorException.BadRequest ex) {
            log.error("Request failed, server return bad request: {}", ex.getMessage());
            var problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
            throw new ClientBadRequestException(extractErrors(problemDetail));
        } catch (HttpClientErrorException ex) {
            log.error("Request failed, server return unhandled exception: {}", ex.getMessage());
            var problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
            throw new ClientServerErrorException(problemDetail);
        }
    }

    private List<String> extractErrors(ProblemDetail error) {
        if (error != null && error.getProperties() != null && error.getProperties().containsKey("errors")) {
            var errors = ((List<?>) error.getProperties().get("errors")).stream()
                    .filter(String.class::isInstance)
                    .map(String::valueOf)
                    .toList();
            log.debug("Error list successfully extracted: {}", errors);
            return errors;
        }
        log.error("Validation error occurred, but no error messages found: {}", error);
        return Collections.emptyList();
    }
}
