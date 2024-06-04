package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.customer.client.exception.ClientServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MethodNotAllowedException;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ClientServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleClientServerErrorException(ClientServerErrorException ex, Model model) {
        log.error("Client server exception: {}", ex.getMessage(), ex);
        model.addAttribute("error", ex.getProblemDetail().getDetail());
        return "errors/500";
    }

    @ExceptionHandler(ClientEntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleClientEntityNotFoundException(ClientEntityNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getProblemDetail().getDetail());
        return "errors/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoResourceFoundException ex) {
        log.error("No handler found", ex);
        return "errors/404";
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handleMethodNotAllowedException(MethodNotAllowedException ex) {
        log.error("Method not allowed", ex);
        return "errors/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex) {
        log.error("Unexpected exception occurred", ex);
        return "errors/500";
    }
}
