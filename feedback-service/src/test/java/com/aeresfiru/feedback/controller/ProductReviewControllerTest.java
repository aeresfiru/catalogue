package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.service.ProductReviewService;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductReviewControllerTest {

    @Mock
    ProductReviewService productReviewService;

    @InjectMocks
    ProductReviewController controller;

    @Test
    void findProductReviewsByProductId_ReturnsProductReviews() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new ProductReview(UUID.fromString("2dbcec0b-686a-4a96-be5a-795b4de19872"), 1, 5,
                        "review#1", "904c6170-f88b-487c-bd1a-4380a637276e"),
                new ProductReview(UUID.fromString("b45be946-6e85-43f8-bcc8-c6927f382d36"), 1, 4,
                        "review#2", "b45be946-6e85-43f8-bcc8-c6927f382d36")
        ))).when(this.productReviewService).findAllProductReviews(1);

        // when
        StepVerifier.create(this.controller.findProductReviews(1))
                // then
                .expectNext(
                        new ProductReview(UUID.fromString("2dbcec0b-686a-4a96-be5a-795b4de19872"), 1, 5,
                                "review#1", "904c6170-f88b-487c-bd1a-4380a637276e"),
                        new ProductReview(UUID.fromString("b45be946-6e85-43f8-bcc8-c6927f382d36"), 1, 4,
                                "review#2", "b45be946-6e85-43f8-bcc8-c6927f382d36")
                )
                .verifyComplete();

        verify(this.productReviewService).findAllProductReviews(1);
        verifyNoMoreInteractions(this.productReviewService);
    }

    @Test
    void createProductReview_ReturnsCreatedProductReview() {
        // given
        String reviewId = "2dbcec0b-686a-4a96-be5a-795b4de19872";
        String userId = "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c";
        var token = new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                .headers(headers -> headers.put("foo", "bar"))
                .claim("sub", userId).build());
        doReturn(Mono.just(new ProductReview(UUID.fromString(reviewId), 1, 4, "review", userId)))
                .when(this.productReviewService)
                .createProductReview(new CreateProductReviewRequest(1, 4, "review"), userId);

        // when
        StepVerifier.create(this.controller.createProductReview(
                        Mono.just(token),
                        Mono.just(new CreateProductReviewRequest(1, 4, "review")),
                        UriComponentsBuilder.fromUriString("http://localhost")))
                // then
                .expectNext(ResponseEntity
                        .created(URI.create("http://localhost/feedback-api/v1/product-reviews/" + reviewId))
                        .body(new ProductReview(UUID.fromString(reviewId), 1, 4, "review", userId)))
                .verifyComplete();

        verify(this.productReviewService).createProductReview(new CreateProductReviewRequest(1, 4, "review"), userId);
        verifyNoMoreInteractions(this.productReviewService);
    }
}
