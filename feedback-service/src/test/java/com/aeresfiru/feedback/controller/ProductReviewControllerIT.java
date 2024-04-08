package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
class ProductReviewControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void init() {
        this.reactiveMongoTemplate.insertAll(List.of(
                new ProductReview(UUID.fromString("2dbcec0b-686a-4a96-be5a-795b4de19872"), 1, 5,
                        "review#1", "904c6170-f88b-487c-bd1a-4380a637276e"),
                new ProductReview(UUID.fromString("b45be946-6e85-43f8-bcc8-c6927f382d36"), 1, 4,
                        "review#2", "b45be946-6e85-43f8-bcc8-c6927f382d36"),
                new ProductReview(UUID.fromString("ddca1b37-6d92-49aa-be1a-ad9e1d7dd3ec"), 3, 5,
                        "review#3", "904c6170-f88b-487c-bd1a-4380a637276e")
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class).all().block();
    }

    @Test
    void findProductReviews_ReturnsReviews() {
        // when
        this.webTestClient.mutateWith(mockJwt())
                .get().uri("/feedback-api/v1/product-reviews/by-product/1")
                .exchange()
                // then
                .expectAll(
                        spec -> spec.expectStatus().isOk(),
                        spec -> spec.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        spec -> spec.expectBodyList(ProductReview.class).hasSize(2).contains(
                                new ProductReview(UUID.fromString("2dbcec0b-686a-4a96-be5a-795b4de19872"), 1, 5,
                                        "review#1", "904c6170-f88b-487c-bd1a-4380a637276e"),
                                new ProductReview(UUID.fromString("b45be946-6e85-43f8-bcc8-c6927f382d36"), 1, 4,
                                        "review#2", "b45be946-6e85-43f8-bcc8-c6927f382d36"))
                );
    }

    @Test
    void findProductReviews_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        // when
        this.webTestClient.get().uri("/feedback-api/v1/product-reviews/by-product/1")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview() {
        // given
        var request = new CreateProductReviewRequest(1, 5, "new review");

        // when
        this.webTestClient.mutateWith(mockJwt())
                .post().uri("/feedback-api/v1/product-reviews")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ProductReview.class)
                .exchange()
                // then
                .expectAll(
                        spec -> spec.expectStatus().isCreated(),
                        spec -> spec.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        spec -> spec.expectHeader().exists(HttpHeaders.LOCATION),
                        spec -> spec.expectBody()
                                .jsonPath("$.id").exists()
                                .jsonPath("$.productId").isEqualTo(1)
                                .jsonPath("$.rating").isEqualTo(5)
                                .jsonPath("$.review").isEqualTo("new review")
                                .jsonPath("$.userId").exists()
                );
    }

    @Test
    void createProductReview_RequestIsInvalid_ReturnsBadRequest() {
        // given
        var request = new CreateProductReviewRequest(null, -1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Massa eget egestas purus viverra accumsan in nisl nisi. Eros donec ac odio tempor. Sodales ut eu sem integer vitae justo. Viverra adipiscing at in tellus integer. Convallis a cras semper auctor. Eros donec ac odio tempor orci dapibus ultrices in. Ultrices in iaculis nunc sed augue lacus viverra vitae congue. Euismod quis viverra nibh cras pulvinar. Aliquet porttitor lacus luctus accumsan.");

        // when
        this.webTestClient.mutateWith(mockJwt())
                .post().uri("/feedback-api/v1/product-reviews")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ProductReview.class)
                .exchange()
                // then
                .expectAll(
                        spec -> spec.expectStatus().isBadRequest(),
                        spec -> spec.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        spec -> spec.expectHeader().doesNotExist(HttpHeaders.LOCATION),
                        spec -> spec.expectBody(ProblemDetail.class)
                );
    }

    @Test
    void createProductReview_UserIsNotAuthenticated_ReturnsUnauthorized() {
        // when
        this.webTestClient.post().uri("/feedback-api/v1/product-reviews")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }
}
