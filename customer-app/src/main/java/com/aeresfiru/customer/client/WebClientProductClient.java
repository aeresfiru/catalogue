package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.customer.client.exception.ClientServerErrorException;
import com.aeresfiru.customer.client.payload.PageResponse;
import com.aeresfiru.customer.client.payload.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class WebClientProductClient implements ProductClient {

    private static final String baseUri = "/catalogue-api/v1/products";

    private final WebClient productWebClient;

    @Override
    public Mono<PageResponse<Product>> findAllProducts(String filter, Integer page, Integer size) {
        return productWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri)
                        .queryParam("filter", filter)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToMono(new ParameterizedTypeReference<PageResponse<Product>>() {
                })
                .doOnError(ex -> log.error("Error retrieving all products with filter: {}", filter, ex))
                .onErrorMap(WebClientResponseException.class,
                        ex -> new ClientServerErrorException(extractProblemDetail(ex)));
    }

    @Override
    public Mono<Product> findProduct(Integer productId) {
        return this.productWebClient.get()
                .uri(baseUri + "/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToMono(Product.class)
                .onErrorMap(WebClientResponseException.NotFound.class,
                        ex -> new ClientEntityNotFoundException(extractProblemDetail(ex)));
    }

    private Mono<? extends Throwable> handleServerError(Mono<ProblemDetail> errorResponse) {
        return errorResponse.flatMap(problemDetail -> {
            log.error("Server error occurred: {}", problemDetail);
            return Mono.error(new ClientServerErrorException(problemDetail));
        });
    }

    private static ProblemDetail extractProblemDetail(WebClientResponseException ex) {
        return ex.getResponseBodyAs(ProblemDetail.class);
    }
}
