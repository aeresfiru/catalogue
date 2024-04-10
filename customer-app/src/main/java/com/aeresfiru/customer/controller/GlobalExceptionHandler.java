package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.customer.client.exception.ClientServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ClientServerErrorException.class)
    public String handleClientServerErrorException(ClientServerErrorException ex, Model model) {
        log.error("Client server exception: {}", ex.getMessage(), ex);
        model.addAttribute("problemDetail", ex.getProblemDetail());
        return "errors/500";
    }

    @ExceptionHandler(ClientEntityNotFoundException.class)
    public String handleClientEntityNotFoundException(ClientEntityNotFoundException ex, Model model) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        model.addAttribute("problemDetail", ex.getProblemDetail());
        return "errors/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model, Locale locale) {
        log.error("Unknown exception occurred: {}", ex.getMessage(), ex);
        var error = this.getMessage("customer.errors.500.title", locale);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, error);
        model.addAttribute("problemDetail", problemDetail);
        return "errors/500";
    }

    private String getMessage(String code, Locale locale) {
        return this.messageSource.getMessage(code, new Object[0], locale);
    }
}
