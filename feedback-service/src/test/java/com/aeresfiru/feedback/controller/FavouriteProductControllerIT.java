package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.entity.FavouriteProduct;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
class FavouriteProductControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        this.reactiveMongoTemplate.insertAll(List.of(
                new FavouriteProduct(UUID.fromString("7c2f09af-9678-4744-91fa-77f2386361fd"), 1,
                        "fe5b0b92-6212-4356-9a52-5f438e747b2a"),
                new FavouriteProduct(UUID.fromString("b54c372f-499a-471c-9307-6f445c65b35f"), 1,
                        "2cb58cb6-5524-4c54-bd85-51b6ca5c9913"),
                new FavouriteProduct(UUID.fromString("697de5c3-2675-4fd9-a295-34ff2c82675c"), 3,
                        "fe5b0b92-6212-4356-9a52-5f438e747b2a")
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(FavouriteProduct.class).all().block();
    }

    @Test
    void findFavouriteProducts_ReturnsFavouriteProducts() {
        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("fe5b0b92-6212-4356-9a52-5f438e747b2a")))
                .get().uri("/feedback-api/v1/favourite-products")
                .exchange()
                // then
                .expectAll(
                        spec -> spec.expectStatus().isOk(),
                        spec -> spec.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        spec -> spec.expectBodyList(FavouriteProduct.class).hasSize(2).contains(
                                new FavouriteProduct(UUID.fromString("7c2f09af-9678-4744-91fa-77f2386361fd"), 1,
                                        "fe5b0b92-6212-4356-9a52-5f438e747b2a"),
                                new FavouriteProduct(UUID.fromString("697de5c3-2675-4fd9-a295-34ff2c82675c"), 3,
                                        "fe5b0b92-6212-4356-9a52-5f438e747b2a"))
                );
    }

    @Test
    void findFavouriteProducts_UserIsNotAuthenticated_ReturnsUnauthorized() {
        // when
        this.webTestClient.get().uri("/feedback-api/v1/favourite-products")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void findFavouriteProductByProductId_ProductInFavourites_ReturnsFavouriteProduct() {
        // when
        this.webTestClient.mutateWith(mockJwt().jwt(builder -> builder.subject("fe5b0b92-6212-4356-9a52-5f438e747b2a")))
                .get().uri("/feedback-api/v1/favourite-products/by-product/1")
                .exchange()
                // then
                .expectAll(
                        spec -> spec.expectStatus().isOk(),
                        spec -> spec.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        spec -> spec.expectBody()
                                .jsonPath("$.id").isEqualTo("7c2f09af-9678-4744-91fa-77f2386361fd")
                                .jsonPath("$.productId").isEqualTo(1)
                                .jsonPath("$.userId").isEqualTo("fe5b0b92-6212-4356-9a52-5f438e747b2a")
                );
    }

    @Test
    void findFavouriteProductByProductId_ProductNotInFavourites_ReturnsNotFound() {
        // when
        this.webTestClient.mutateWith(mockJwt().jwt(builder -> builder.subject("fe5b0b92-6212-4356-9a52-5f438e747b2a")))
                .get().uri("/feedback-api/v1/favourite-products/by-product/2")
                .exchange()
                // then
                .expectAll(
                        spec -> spec.expectStatus().isNotFound(),
                        spec -> spec.expectBody(ProblemDetail.class)
                );
    }

    @Test
    void findFavouriteProductByProductId_UserIsNotAuthenticated_ReturnsUnauthorized() {
        // when
        this.webTestClient.get().uri("/feedback-api/v1/favourite-products/by-product/3")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void addProductToFavourites_RequestIsValid_ReturnsCreatedFavouriteProduct() {
        // when
        this.webTestClient.mutateWith(mockJwt().jwt(builder -> builder.subject("fe5b0b92-6212-4356-9a52-5f438e747b2a")))
                .post().uri("/feedback-api/v1/favourite-products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"productId\": 1}")
                .exchange()
                // then
                .expectAll(
                        spec -> spec.expectStatus().isCreated(),
                        spec -> spec.expectHeader().exists(HttpHeaders.LOCATION),
                        spec -> spec.expectBody()
                                .jsonPath("$.id").exists()
                                .jsonPath("$.productId").isEqualTo(1)
                                .jsonPath("$.userId").isEqualTo("fe5b0b92-6212-4356-9a52-5f438e747b2a")
                );
    }

    @Test
    void addProductToFavourites_RequestIsInvalid_ReturnsBadRequest() {
        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("fe5b0b92-6212-4356-9a52-5f438e747b2a")))
                .post().uri("/feedback-api/v1/favourite-products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"productId\": null}")
                .exchange()
                // then
                .expectAll(
                        spec -> spec.expectStatus().isBadRequest(),
                        spec -> spec.expectHeader().doesNotExist(HttpHeaders.LOCATION),
                        spec -> spec.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        spec -> spec.expectBody(ProblemDetail.class)
                );
    }

    @Test
    void addFavouriteProduct_UserIsNotAuthenticated_ReturnsUnauthorized() {
        // when
        this.webTestClient.post().uri("/feedback-api/v1/favourite-products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"productId\": 1}")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void removeProductFromFavourites_ReturnsNoContent() {
        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("fe5b0b92-6212-4356-9a52-5f438e747b2a")))
                .delete().uri("/feedback-api/v1/favourite-products/by-product/1")
                .exchange()
                // then
                .expectStatus().isNoContent();
    }

    @Test
    void removeProductFromFavourites_UserIsNotAuthenticated_ReturnsUnauthorized() {
        // when
        this.webTestClient.delete().uri("/feedback-api/v1/favourite-products/by-product/1")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }
}
