package com.aeresfiru.customer.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@SpringBootTest
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 54321)
class ProductControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        stubFor(get("/catalogue-api/v1/products/1")
                .willReturn(okJson("""
                        {
                            "id": 1,
                            "title": "Product title #1",
                            "details": "Product details #1"
                        }""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
        stubFor(get("/feedback-api/v1/favourite-products")
                .willReturn(okJson("""
                        {
                            "id": "316065cd-e93d-4c82-a997-9fc9a43cfc78",
                            "productId": 1
                        }
                        """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
        stubFor(get("/feedback-api/v1/product-reviews?productId=1")
                .willReturn(okJson("""
                        [
                            {
                                "id": "316065cd-e93d-4c82-a997-9fc9a43cfc78",
                                "productId": 1,
                                "rating": 3,
                                "review": "Nice",
                                "userId": "a1f39ba2-ab84-49df-93b7-476638dbc715"
                            },
                            {
                                "id": "452bff16-d86a-4cc3-9eb7-a823a6d32e7f",
                                "productId": 1,
                                "rating": 5,
                                "review": "Great!",
                                "userId": "e003e808-2b8f-4ad0-b37a-87636467f350"
                            }
                        ]""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    void getProductPage_ProductExists_ReturnsProductPage() {
        // given

        // when
        this.webTestClient
                .mutateWith(mockUser())
                .get()
                .uri("/customer/products/1")
                .exchange()
                // then
                .expectStatus().isOk();

        verify(getRequestedFor(urlPathMatching("/catalogue-api/v1/products/1")));
        verify(getRequestedFor(urlPathMatching("/feedback-api/v1/product-reviews")));
        verify(getRequestedFor(urlPathMatching("/feedback-api/v1/favourite-products")));
    }

    @Test
    void getProductPage_UserIsNotAuthorized_RedirectsToLoginPage() {
        // given

        // when
        this.webTestClient
                .get()
                .uri("/customer/products/1")
                .exchange()
                // then
                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

    @Test
    void addProductToFavourites_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        stubFor(post("/feedback-api/v1/favourite-products")
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1
                        }"""))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(created()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "e9821e7e-561a-4778-a3b5-c1791904f7b4",
                                    "productId": 1
                                }""")));

        // when
        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-to-favourites")
                .exchange()
                // then
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/products/1");

        verify(getRequestedFor(urlPathMatching("/catalogue-api/v1/products/1")));
        verify(postRequestedFor(urlPathMatching("/feedback-api/v1/favourite-products"))
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1
                        }""")));
    }

    @Test
    void addProductToFavourites_ProductDoesNotExist_ReturnsNotFoundPage() {
        // given
        stubFor(get("/catalogue-api/v1/products/404")
                .willReturn(notFound()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                {
                                    "title": "Product not found",
                                    "detail": "Product with the given ID: 404 was not found"
                                }
                                """)));

        // when
        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/404/add-to-favourites")
                .exchange()
                // then
                .expectStatus().isNotFound();

        verify(getRequestedFor(urlPathMatching("/catalogue-api/v1/products/404")));
    }

    @Test
    void addProductToFavourites_UserIsNotAuthorized_RedirectsToLoginPage() {
        // given

        // when
        this.webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-to-favourites")
                .exchange()
                // then
                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

    @Test
    void removeProductFromFavourites_ProductExists_ReturnsRedirectionToProductPage() {
        // given
        stubFor(delete("/feedback-api/v1/favourite-products?productId=1")
                .willReturn(noContent()));

        // when
        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/remove-from-favourites")
                .exchange()
                // then
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/products/1");

        verify(getRequestedFor(urlPathMatching("/catalogue-api/v1/products/1")));
        verify(deleteRequestedFor(urlPathMatching("/feedback-api/v1/favourite-products")));
    }

    @Test
    void removeProductFromFavourites_UserIsNotAuthorized_RedirectsToLoginPage() {
        // given

        // when
        this.webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/remove-from-favourites")
                .exchange()
                // then
                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

    @Test
    void createReview_RequestIsValid_RedirectsToProductPage() {
        // given
        stubFor(post("/feedback-api/v1/product-reviews")
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1,
                            "rating": 3,
                            "review": "Ну, на троечку..."
                        }"""))
                .willReturn(created()
                        .withHeader(HttpHeaders.LOCATION, "http://localhost/feedback-api/v1/product-reviews/b852bc8e-cbc5-11ee-bbc5-bf192e2492e5")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "b852bc8e-cbc5-11ee-bbc5-bf192e2492e5",
                                    "productId": 1,
                                    "rating": 3,
                                    "review": "Ну, на троечку...",
                                    "userId": "1a24d4ec-cbc6-11ee-af3b-0b236022162c"
                                }""")));

        // when
        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/create-review")
                .body(BodyInserters.fromFormData("rating", "3")
                        .with("review", "Ну, на троечку..."))
                // then
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/products/1");

        verify(postRequestedFor(urlPathMatching("/feedback-api/v1/product-reviews"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1,
                            "rating": 3,
                            "review": "Ну, на троечку..."
                        }""")));
    }

    @Test
    void createReview_RequestIsInvalid_ReturnsProductPage() throws Exception {
        // given
        stubFor(post("/feedback-api/v1/product-reviews")
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1,
                            "rating": -1,
                            "review": "Ну очень длинный отзыв (да, тут более 1000 символов)"
                        }"""))
                .willReturn(badRequest()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                {
                                    "errors": ["Ошибка 1", "Ошибка 2"]
                                }""")));

        // when
        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/create-review")
                .body(BodyInserters.fromFormData("rating", "-1")
                        .with("review", "Ну очень длинный отзыв (да, тут более 1000 символов)"))
                // then
                .exchange()
                .expectStatus().isBadRequest();

        verify(postRequestedFor(urlPathMatching("/feedback-api/v1/product-reviews"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1,
                            "rating": -1,
                            "review": "Ну очень длинный отзыв (да, тут более 1000 символов)"
                        }""")));
    }

    @Test
    void createReview_ProductDoesNotExist_ReturnsNotFoundPage() {
        // given
        stubFor(get("/catalogue-api/v1/products/404")
                .willReturn(notFound()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                {
                                    "title": "Product not found",
                                    "detail": "Product with the given ID: 404 was not found"
                                }
                                """)));

        // when
        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/404/create-review")
                .body(BodyInserters.fromFormData("rating", "5")
                        .with("review", "The best!"))
                .exchange()
                // then
                .expectStatus().isNotFound();

        verify(getRequestedFor(urlPathMatching("/catalogue-api/v1/products/404")));
    }

    @Test
    void createReview_UserIsNotAuthorized_RedirectsToLoginPage() {
        // given

        // when
        this.webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/create-review")
                .body(BodyInserters.fromFormData("rating", "3")
                        .with("review", "Ну, на троечку..."))
                .exchange()
                // then
                .expectStatus().isFound()
                .expectHeader().location("/login");
    }
}