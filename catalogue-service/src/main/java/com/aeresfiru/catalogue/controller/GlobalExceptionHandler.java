package com.aeresfiru.catalogue.controller;

import com.aeresfiru.catalogue.service.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String errorMessage = this.getMessage("catalogue.errors.400.title", request.getLocale());
        var errors = this.getErrors(ex, request);
        var problemDetail = ProblemDetail.forStatusAndDetail(status, errorMessage);
        problemDetail.setStatus(status.value());
        problemDetail.setProperty("errors", errors);
        return ResponseEntity.status(status).headers(headers).body(problemDetail);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleProductNotFoundException(ProductNotFoundException ex, Locale locale) {
        String errorMessage = this.getMessage(ex.getMessage(), locale);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, errorMessage);
        problemDetail.setTitle(getMessage("catalogue.errors.404.title", locale));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, Locale locale) {
        String errorMessage = this.getMessage("catalogue.errors.500.detail", locale);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        problemDetail.setTitle(getMessage("catalogue.errors.500.title", locale));
        return problemDetail;
    }

    private List<String> getErrors(MethodArgumentNotValidException ex, WebRequest request) {
        return ex.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .map(msg -> this.getMessage(msg, request.getLocale()))
                .collect(Collectors.toList());
    }

    private String getMessage(String name, Locale locale) {
        return this.messageSource.getMessage(name, new Object[0], name, locale);
    }
}
