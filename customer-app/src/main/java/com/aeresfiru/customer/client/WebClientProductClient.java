package com.aeresfiru.customer.client;

import com.aeresfiru.customer.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientProductClient implements ProductClient {

    private static final String baseUri = "/catalogue-api/v1/products";

    private final WebClient productWebClient;

    private final ErrorHandler errorHandler;

    @Override
    public Flux<Product> findAllProducts(String filter, Integer page, Integer size) {
        return this.productWebClient.get()
                .uri(baseUri + "?filter={filter}", filter)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> errorHandler.handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToFlux(Product.class)
                .doOnError(ex -> log.error("Error retrieving all products with filter: {}", filter, ex));
    }

    @Override
    public Mono<Product> findProduct(Integer productId) {
        return this.productWebClient.get()
                .uri(baseUri + "/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> errorHandler.handleClientError(response.bodyToMono(ProblemDetail.class)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> errorHandler.handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToMono(Product.class)
                .doOnError(ex -> log.error("Error retrieving product by ID {}", productId, ex))
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }
}
