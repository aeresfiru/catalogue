package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
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

    @DisplayName("The endpoint returns favourite products correctly")
    @Test
    void findFavouriteProducts_ReturnsFavouriteProducts() {
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("fe5b0b92-6212-4356-9a52-5f438e747b2a")))
                .get().uri("/feedback-api/v1/favourite-products")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        [
                            {
                               "id": "7c2f09af-9678-4744-91fa-77f2386361fd",
                               "productId": 1,
                               "userId": "fe5b0b92-6212-4356-9a52-5f438e747b2a"
                            },
                            {
                                "id": "697de5c3-2675-4fd9-a295-34ff2c82675c",
                                "productId": 3,
                                "userId": "fe5b0b92-6212-4356-9a52-5f438e747b2a"
                            }
                        ]""")
                .consumeWith(document("feedback/favourite-products/find",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Favourite product ID"),
                                fieldWithPath("[].productId").description("Product ID"),
                                fieldWithPath("[].userId").description("User ID")
                        )
                ));
    }

    @DisplayName("Unauthorized users cannot access the endpoint")
    @Test
    void findFavouriteProducts_UserIsNotAuthenticated_ReturnsUnauthorized() {
        this.webTestClient
                .get().uri("/feedback-api/v1/favourite-products")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .consumeWith(document("feedback/favourite-products/find-unauthorized"))
                .isEmpty();
    }

    @DisplayName("A product can be added to favourites successfully")
    @Test
    void addProductToFavourites_RequestIsValid_ReturnsCreatedFavouriteProduct() {
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("fe5b0b92-6212-4356-9a52-5f438e747b2a")))
                .post().uri("/feedback-api/v1/favourite-products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"productId\": 1}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists()
                .json("""
                        {
                            "productId":  1,
                            "userId": "fe5b0b92-6212-4356-9a52-5f438e747b2a"
                        }""")
                .consumeWith(document("feedback/favourite-products/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type("int").description("Product ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Favourite product ID"),
                                fieldWithPath("productId").description("Product ID"),
                                fieldWithPath("userId").description("User ID")
                        )));
    }

    @DisplayName("An invalid request to add a product to favourites returns a bad request")
    @Test
    void addProductToFavourites_RequestIsInvalid_ReturnsBadRequest() {
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("fe5b0b92-6212-4356-9a52-5f438e747b2a")))
                .post().uri("/feedback-api/v1/favourite-products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"productId\": null}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .consumeWith(document("feedback/favourite-products/add-invalid-request",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("type").description("Error type"),
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail message"),
                                fieldWithPath("instance").description("URI of the request that caused the error"),
                                fieldWithPath("errors").description("List of invalid parameters and their errors")
                        )));
    }

    @DisplayName("Unauthorized users cannot add a product to favourites")
    @Test
    void addFavouriteProduct_UserIsNotAuthenticated_ReturnsUnauthorized() {
        this.webTestClient
                .post().uri("/feedback-api/v1/favourite-products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"productId\": 1}")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().consumeWith(document("feedback/favourite-products/add-unauthorized"));
    }

    @DisplayName("A product can be removed from favourites successfully")
    @Test
    void removeProductFromFavourites_ReturnsNoContent() {
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("fe5b0b92-6212-4356-9a52-5f438e747b2a")))
                .delete().uri("/feedback-api/v1/favourite-products?productId=1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(document("feedback/favourite-products/remove"))
                .isEmpty();
    }

    @DisplayName("Unauthorized users cannot remove a product from favourites")
    @Test
    void removeProductFromFavourites_UserIsNotAuthenticated_ReturnsUnauthorized() {
        this.webTestClient
                .delete().uri("feedback-api/v1/favourite-products?productId=1")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .consumeWith(document("feedback/favourite-products/remove-unauthorized"))
                .isEmpty();
    }
}
