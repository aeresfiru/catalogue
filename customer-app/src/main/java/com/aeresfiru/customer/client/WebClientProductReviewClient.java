package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.exception.ClientBadRequestException;
import com.aeresfiru.customer.client.exception.ClientServerErrorException;
import com.aeresfiru.customer.client.payload.CreateProductReviewRequest;
import com.aeresfiru.customer.client.payload.ProductReview;
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

@RequiredArgsConstructor
@Slf4j
public class WebClientProductReviewClient implements ProductReviewClient {

    private final static String baseUri = "/feedback-api/v1/product-reviews";

    private final WebClient feedbackWebClient;

    @Override
    public Flux<ProductReview> findProductReviewsByProductId(Integer productId) {
        return feedbackWebClient.get()
                .uri(builder -> builder
                        .path(baseUri)
                        .queryParam("productId", productId)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToFlux(ProductReview.class)
                .doOnError(ex -> log.error("Error retrieving product reviews by product ID: {}", productId, ex));
    }

    @Override
    public Mono<ProductReview> createProductReview(CreateProductReviewRequest request) {
        return feedbackWebClient.post()
                .uri(baseUri)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleServerError(response.bodyToMono(ProblemDetail.class)))
                .bodyToMono(ProductReview.class)
                .doOnError(ex -> log.error("Error creating product review: {}", request, ex))
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        ex -> new ClientBadRequestException(extractErrors(ex)));
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
