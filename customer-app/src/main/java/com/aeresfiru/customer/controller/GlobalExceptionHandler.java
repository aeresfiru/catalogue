package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.customer.client.exception.ClientServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ClientServerErrorException.class)
    public Mono<String> handleServerErrorException(ClientServerErrorException ex, Model model) {
        log.error("Client server exception: {}", ex.getMessage(), ex);
        model.addAttribute("error", ex.getMessage());
        return Mono.just("errors/500");
    }

    @ExceptionHandler(ClientEntityNotFoundException.class)
    public Mono<String> handleEntityNotFoundException(ClientEntityNotFoundException ex, Model model) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        model.addAttribute("error", ex.getMessage());
        return Mono.just("errors/404");
    }

    @ExceptionHandler(Exception.class)
    public Mono<String> handleException(Exception ex, Model model, Locale locale) {
        log.error("Unknown exception occurred: {}", ex.getMessage(), ex);
        var error = this.getMessage("customer.errors.500.title", locale);
        model.addAttribute("error", error);
        return Mono.just("errors/500");
    }

    private String getMessage(String code, Locale locale) {
        return this.messageSource.getMessage(code, new Object[0], locale);
    }
}
