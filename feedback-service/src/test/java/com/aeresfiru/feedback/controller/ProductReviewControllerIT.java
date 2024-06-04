package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProductReviewControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void init() {
        this.reactiveMongoTemplate.insertAll(List.of(
                new ProductReview(UUID.fromString("2dbcec0b-686a-4a96-be5a-795b4de19872"), 1, 5,
                        "Great product!", "904c6170-f88b-487c-bd1a-4380a637276e"),
                new ProductReview(UUID.fromString("b45be946-6e85-43f8-bcc8-c6927f382d36"), 1, 4,
                        "Kinda like it", "b45be946-6e85-43f8-bcc8-c6927f382d36"),
                new ProductReview(UUID.fromString("ddca1b37-6d92-49aa-be1a-ad9e1d7dd3ec"), 3, 5,
                        "Awesome! Use it all day long", "904c6170-f88b-487c-bd1a-4380a637276e")
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class).all().block();
    }

    @DisplayName("The endpoint returns product reviews correctly")
    @Test
    void findProductReviews_ReturnsReviews() {
        this.webTestClient
                .mutateWith(mockJwt())
                .get().uri("/feedback-api/v1/product-reviews?productId=1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        [
                                {
                                    "id": "2dbcec0b-686a-4a96-be5a-795b4de19872",
                                    "productId": 1,
                                    "rating": 5,
                                    "review": "Great product!",
                                    "userId": "904c6170-f88b-487c-bd1a-4380a637276e"
                                },
                                {
                                    "id": "b45be946-6e85-43f8-bcc8-c6927f382d36",
                                    "productId": 1,
                                    "rating": 4,
                                    "review": "Kinda like it",
                                    "userId": "b45be946-6e85-43f8-bcc8-c6927f382d36"
                                }
                            ]""")
                .consumeWith(document("feedback/product-reviews/find",
                        preprocessResponse(prettyPrint()),
                        relaxedResponseFields(
                                fieldWithPath("[].id").description("Review ID"),
                                fieldWithPath("[].productId").description("Product ID"),
                                fieldWithPath("[].rating").description("Rating"),
                                fieldWithPath("[].review").description("Review content"),
                                fieldWithPath("[].userId").description("User ID")
                        )
                ));
    }

    @DisplayName("A product review can be successfully created")
    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview() {
        var request = new CreateProductReviewRequest(1, 5, "Haven't seen anything better than this");

        this.webTestClient
                .mutateWith(mockJwt())
                .post().uri("/feedback-api/v1/product-reviews")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ProductReview.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.userId").exists()
                .json("""
                        {
                            "productId": 1,
                            "rating": 5,
                            "review": "Haven't seen anything better than this"
                        }""")
                .consumeWith(document("feedback/product-reviews/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type("int").description("Product ID"),
                                fieldWithPath("rating").type("int").description("Rating"),
                                fieldWithPath("review").type("string").description("Review content")
                        ),
                        responseFields(
                                fieldWithPath("id").type("uuid").description("Review ID"),
                                fieldWithPath("productId").type("int").description("Product ID"),
                                fieldWithPath("rating").type("int").description("Rating"),
                                fieldWithPath("review").type("string").description("Review content"),
                                fieldWithPath("userId").type("string").description("User ID")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Link to created product review")
                        )
                ));
    }

    @DisplayName("An invalid product review request returns a bad request response")
    @Test
    void createProductReview_RequestIsInvalid_ReturnsBadRequest() {
        var request = new CreateProductReviewRequest(null, -1, "");

        this.webTestClient
                .mutateWith(mockJwt())
                .post().uri("/feedback-api/v1/product-reviews")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ProductReview.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectBody(ProblemDetail.class)
                .consumeWith(document("feedback/product-reviews/create-invalid-request",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("type").description("Error type"),
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail message"),
                                fieldWithPath("instance").description("URI of the request that caused the error"),
                                fieldWithPath("errors").description("List of invalid parameters and their errors")
                        )
                ));
    }

    @DisplayName("Unauthorized users cannot create a product review")
    @Test
    void createProductReview_UserIsNotAuthenticated_ReturnsUnauthorized() {
        this.webTestClient
                .post().uri("/feedback-api/v1/product-reviews")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().consumeWith(document("feedback/product-reviews/create-unauthorized"));
    }
}
