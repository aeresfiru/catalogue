package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.payload.CreateProductReviewRequest;
import com.aeresfiru.customer.entity.ProductReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientProductReviewClient implements ProductReviewClient {

    private final static String baseUri = "/feedback-api/v1/product-reviews";

    private final WebClient feedbackWebClient;

    private final ErrorHandler errorHandler;

    @Override
    public Flux<ProductReview> findProductReviewsByProductId(Integer productId) {
        return feedbackWebClient.get()
                .uri(baseUri + "/by-product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> errorHandler.handleClientError(response.bodyToMono(ProblemDetail.class)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> errorHandler.handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToFlux(ProductReview.class)
                .doOnError(ex -> log.error("Error retrieving product reviews by product ID: {}", productId, ex));
    }

    @Override
    public Mono<ProductReview> createProductReview(CreateProductReviewRequest request) {
        return feedbackWebClient.post()
                .uri(baseUri)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> errorHandler.handleClientError(response.bodyToMono(ProblemDetail.class)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> errorHandler.handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToMono(ProductReview.class)
                .doOnError(ex -> log.error("Error creating product review: {}", request, ex));
    }
}
