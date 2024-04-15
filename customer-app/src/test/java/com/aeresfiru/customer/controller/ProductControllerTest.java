package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.client.exception.ClientBadRequestException;
import com.aeresfiru.customer.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.customer.client.payload.*;
import com.aeresfiru.customer.service.FavouriteProductService;
import com.aeresfiru.customer.service.ProductReviewService;
import com.aeresfiru.customer.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductService productService;

    @Mock
    FavouriteProductService favouriteProductService;

    @Mock
    ProductReviewService productReviewService;

    @InjectMocks
    ProductController controller;

    @Test
    void product_ProductExists_ReturnsNotEmptyMono() {
        // given
        var product = new Product(1, "Product Title", "Product Details");
        doReturn(Mono.just(product)).when(this.productService).findProduct(1);

        // when
        StepVerifier.create(this.controller.product(1))
                // then
                .expectNext(new Product(1, "Product Title", "Product Details"))
                .expectComplete()
                .verify();
    }

    @Test
    void product_ProductDoesNotExist_ThrowsClientEntityNotFoundException() {
        // given
        var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        doReturn(Mono.error(new ClientEntityNotFoundException(problemDetail)))
                .when(this.productService).findProduct(1);

        // when
        StepVerifier.create(this.controller.product(1))
                //then
                .expectErrorMatches(ex -> ex instanceof ClientEntityNotFoundException e
                        && e.getProblemDetail() != null
                        && e.getProblemDetail().getStatus() == HttpStatus.NOT_FOUND.value())
                .verify();
    }

    @Test
    void isFavourite_ProductInFavourites_ReturnsTrue() {
        // given
        doReturn(Mono.just(true)).when(this.favouriteProductService).isProductInFavourites(1);

        // when
        StepVerifier.create(this.controller.isFavourite(1))
                // then
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isFavourite_ProductNotInFavourites_ReturnsFalse() {
        // given
        doReturn(Mono.just(false)).when(this.favouriteProductService).isProductInFavourites(1);

        // when
        StepVerifier.create(this.controller.isFavourite(1))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void productReviews_RequestIsValid_ReturnsProductReviews() {
        // given
        doReturn(Flux.fromIterable(List.of(
                        new ProductReview("cba6250c-b3d0-4bf1-b546-55960196f1d7", 1, 5, "review#1"),
                        new ProductReview("1b2fa64d-d798-488a-991e-47c0c52a816a", 1, 4, "review#2")
                ))
        ).when(this.productReviewService).findProductReviews(1);

        // when
        StepVerifier.create(this.controller.productReviews(1))
                // then
                .expectNext(
                        new ProductReview("cba6250c-b3d0-4bf1-b546-55960196f1d7", 1, 5, "review#1"),
                        new ProductReview("1b2fa64d-d798-488a-991e-47c0c52a816a", 1, 4, "review#2")
                )
                .expectComplete()
                .verify();
    }

    @Test
    void productReviews_ProductNotFound_Returns404() {
        // given
        doReturn(Flux.error(new ClientEntityNotFoundException(ProblemDetail.forStatus(HttpStatus.NOT_FOUND))))
                .when(this.productReviewService).findProductReviews(1);

        // when
        StepVerifier.create(this.controller.productReviews(1))
                // then
                .expectError(ClientEntityNotFoundException.class)
                .verify();

        verify(this.productReviewService).findProductReviews(1);
        verifyNoMoreInteractions(this.productReviewService);
        verifyNoInteractions(this.productService, this.favouriteProductService);
    }

    @Test
    void showProductPage_ReturnsProductPage() {
        // when
        StepVerifier.create(this.controller.showProductPage())
                // then
                .expectNext("customer/products/product")
                .verifyComplete();
    }

    @Test
    void addProductToFavourites_RequestIsValid_ReturnsProductPage() {
        // given
        doReturn(Mono.just(new FavouriteProduct("cba6250c-b3d0-4bf1-b546-55960196f1d7", 1)))
                .when(this.favouriteProductService).addProductToFavourites(new CreateFavouriteProductRequest(1));

        // when
        StepVerifier.create(this.controller.addProductToFavourites(1))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(this.favouriteProductService).addProductToFavourites(new CreateFavouriteProductRequest(1));
        verifyNoMoreInteractions(this.favouriteProductService);
        verifyNoInteractions(this.productService, this.productService);
    }

    @Test
    void addProductToFavourites_RequestIsInvalid_RedirectsToProductPage() {
        // given
        doReturn(Mono.error(new ClientBadRequestException(Collections.singletonList("description"))))
                .when(this.favouriteProductService).addProductToFavourites(new CreateFavouriteProductRequest(1));

        // when
        StepVerifier.create(this.controller.addProductToFavourites(1))
                // then
                .expectErrorMatches(error -> error instanceof ClientBadRequestException e
                        && e.getErrors().contains("description"))
                .verify();

        verify(this.favouriteProductService).addProductToFavourites(new CreateFavouriteProductRequest(1));
        verifyNoMoreInteractions(this.favouriteProductService);
        verifyNoInteractions(this.productService, this.productService);
    }

    @Test
    void removeProductFromFavourites_RedirectsToProductPage() {
        // given
        doReturn(Mono.empty()).when(this.favouriteProductService).removeProductFromFavourites(1);

        // when
        StepVerifier.create(this.controller.removeProductFromFavourites(1))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(this.favouriteProductService).removeProductFromFavourites(1);
        verifyNoMoreInteractions(this.favouriteProductService);
        verifyNoInteractions(this.productService, this.productReviewService);
    }

    @Test
    void createReview_RequestIsValid_RedirectsToProductPage() {
        // given
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        doReturn(Mono.just(new ProductReview("86efa22c-cbae-11ee-ab01-679baf165fb7", 1, 3, "review")))
                .when(this.productReviewService).createProductReview(new CreateProductReviewRequest(1, 3, "review"));

        // when
        StepVerifier.create(this.controller.createReview(Mono.just(new Product(1, "Title", "Description")),
                        Mono.just(new CreateProductReviewRequest(1, 3, "review")), model, response))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(this.productReviewService).createProductReview(new CreateProductReviewRequest(1, 3, "review"));
        verifyNoMoreInteractions(this.productReviewService);
        verifyNoInteractions(this.productService, this.favouriteProductService);
    }

    @Test
    void createReview_RequestIsInvalid_ReturnsProductPageWithPayloadAndErrors() {
        // given
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        doReturn(Mono.error(new ClientBadRequestException(Collections.singletonList("rating null"))))
                .when(this.productReviewService).createProductReview(new CreateProductReviewRequest(1, null, null));

        // when
        StepVerifier.create(this.controller.createReview(
                        Mono.just(new Product(1, "title", "description")),
                        Mono.just(new CreateProductReviewRequest(1, null, null)), model, response))
                // then
                .expectNext("customer/products/product")
                .verifyComplete();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(model.getAttribute("payload")).isEqualTo(new CreateProductReviewRequest(1, null, null));
        assertThat(model.getAttribute("errors")).isEqualTo(Collections.singletonList("rating null"));

        verify(this.productReviewService).createProductReview(new CreateProductReviewRequest(1, null, null));
        verifyNoMoreInteractions(this.productReviewService, this.favouriteProductService);
    }
}
