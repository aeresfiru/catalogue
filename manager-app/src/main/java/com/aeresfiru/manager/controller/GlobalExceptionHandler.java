package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.manager.client.exception.ClientServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ClientServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleClientServerErrorException(ClientServerErrorException ex, Model model) {
        log.error("Client server exception, detail: {}", ex.getProblemDetail(), ex);
        model.addAttribute("problemDetail", ex.getProblemDetail());
        return "errors/500";
    }

    @ExceptionHandler(ClientEntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(ClientEntityNotFoundException ex, Model model) {
        log.error("Resource not found, detail: {}", ex.getProblemDetail(), ex);
        model.addAttribute("problemDetail", ex.getProblemDetail());
        return "errors/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoResourceFoundException ex, Model model) {
        log.error("No handler found", ex);
        return "errors/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model, Locale locale) throws Exception {
        if (ex instanceof ClientAuthorizationRequiredException) {
            throw ex;
        }
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        var error = this.getMessage("customer.errors.500.title", locale);
        model.addAttribute("error", error);
        return "errors/500";
    }

    private String getMessage(String code, Locale locale) {
        return this.messageSource.getMessage(code, new Object[0], locale);
    }
}
