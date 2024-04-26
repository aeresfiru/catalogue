package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.exception.ClientBadRequestException;
import com.aeresfiru.customer.client.exception.ClientServerErrorException;
import com.aeresfiru.customer.client.payload.CreateFavouriteProductRequest;
import com.aeresfiru.customer.client.payload.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class WebClientFavouriteProductClient implements FavouriteProductClient {

    private static final String BASE_URI = "/feedback-api/v1/favourite-products";

    private final WebClient feedbackWebClient;

    @Override
    public Flux<FavouriteProduct> findAllFavouriteProducts() {
        return feedbackWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URI)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToFlux(FavouriteProduct.class)
                .doOnError(ex -> log.error("Error retrieving all favourite products", ex));
    }

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request) {
        return feedbackWebClient.post()
                .uri(BASE_URI)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToMono(FavouriteProduct.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        ex -> new ClientBadRequestException(extractErrors(ex)))
                .doOnError(ex -> log.error("Error adding product to favourites: {}", request, ex));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(Integer productId) {
        return feedbackWebClient.delete()
                .uri(builder -> {
                    var uri = builder
                            .path(BASE_URI)
                            .queryParam("productId", productId)
                            .build();
                    log.info("URI: {}", uri);
                    return uri;
                })
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleServerError(response.bodyToMono(ProblemDetail.class)))
                .toBodilessEntity()
                .then()
                .doOnError(ex -> log.error("Error removing product from favourites with ID: {}", productId, ex));
    }

    private static Mono<? extends Throwable> handleServerError(Mono<ProblemDetail> errorResponse) {
        return errorResponse.flatMap(problemDetail -> {
            log.error("Server error occurred: {}", problemDetail);
            return Mono.error(new ClientServerErrorException(problemDetail));
        });
    }

    private static List<String> extractErrors(WebClientResponseException.BadRequest ex) {
        var problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
        if (!isResponseBodyValid(problemDetail)) {
            log.error("Validation error occurred, but no error response found: {}", problemDetail);
            return Collections.emptyList();
        }
        return extractErrorList(problemDetail);
    }

    private static boolean isResponseBodyValid(ProblemDetail problemDetail) {
        return problemDetail != null
                && problemDetail.getProperties() != null
                && problemDetail.getProperties().containsKey("errors");
    }

    private static List<String> extractErrorList(ProblemDetail problemDetail) {
        return ((List<?>) problemDetail.getProperties().get("errors")).stream()
                .filter(String.class::isInstance)
                .map(String::valueOf)
                .toList();
    }
}
