package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.customer.client.exception.ClientServerErrorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.ui.ConcurrentModel;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    MessageSource messageSource;

    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleClientServerErrorException_Returns500() {
        // given
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "detail");
        var ex = new ClientServerErrorException(problemDetail);
        var model = new ConcurrentModel();

        // when
        var result = this.globalExceptionHandler.handleClientServerErrorException(ex, model);

        // then
        assertThat(result).isEqualTo("errors/500");
        assertThat(model.getAttribute("error")).isNotNull();
    }

    @Test
    void handleClientEntityNotFoundException_ReturnsError404() {
        // given
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "detail");
        var ex = new ClientEntityNotFoundException(problemDetail);
        var model = new ConcurrentModel();

        // when
        var result = this.globalExceptionHandler.handleClientEntityNotFoundException(ex, model);

        // then
        assertThat(result).isEqualTo("errors/404");
        assertThat(model.getAttribute("error")).isNotNull();
    }

    @Test
    void handleException_Returns500() {
        // given

        // when
        var result = this.globalExceptionHandler.handleException(new RuntimeException());

        // then
        assertThat(result).isEqualTo("errors/500");
    }
}
