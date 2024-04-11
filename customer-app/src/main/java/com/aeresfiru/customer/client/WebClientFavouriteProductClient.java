package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.payload.CreateFavouriteProductRequest;
import com.aeresfiru.customer.entity.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientFavouriteProductClient implements FavouriteProductClient {

    private static final String BASE_URI = "/feedback-api/v1/favourite-products";

    private final WebClient feedbackWebClient;

    private final ErrorHandler errorHandler;

    @Override
    public Flux<FavouriteProduct> findAllFavouriteProducts() {
        return feedbackWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URI)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> errorHandler.handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToFlux(FavouriteProduct.class)
                .doOnError(ex -> log.error("Error retrieving all favourite products", ex));
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProductId(Integer productId) {
        return feedbackWebClient.get()
                .uri(BASE_URI + "/by-product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> errorHandler.handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToMono(FavouriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class)
                .doOnError(ex -> log.error("Error retrieving favourite product by ID: {}", productId, ex));
    }

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request) {
        return feedbackWebClient.post()
                .uri(BASE_URI)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> errorHandler.handleClientError(response.bodyToMono(ProblemDetail.class)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> errorHandler.handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToMono(FavouriteProduct.class)
                .doOnError(ex -> log.error("Error adding product to favourites: {}", request, ex));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(Integer productId) {
        return feedbackWebClient.delete()
                .uri(BASE_URI + "/by-product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> errorHandler.handleServerError(response.bodyToMono(ProblemDetail.class)))
                .toBodilessEntity()
                .then()
                .doOnError(ex -> log.error("Error removing product from favourites with ID: {}", productId, ex));
    }
}
