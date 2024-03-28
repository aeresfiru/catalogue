package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.exception.BadRequestClientException;
import com.aeresfiru.customer.client.payload.CreateProductReviewRequest;
import com.aeresfiru.customer.entity.ProductReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebClientProductReviewClient implements ProductReviewClient {

    private final static String baseUri = "/feedback-api/v1/product-reviews";

    private final WebClient feedbackWebClient;

    @Override
    public Flux<ProductReview> findProductReviewsByProductId(Integer productId) {
        return feedbackWebClient.get()
                .uri(baseUri + "/by-product/{productId}", productId)
                .retrieve()
                .bodyToFlux(ProductReview.class);
    }

    @Override
    public Mono<ProductReview> createProductReview(CreateProductReviewRequest request) {
        return feedbackWebClient.post()
                .uri(baseUri)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ProductReview.class)
                .onErrorMap(WebClientResponseException.BadRequest.class, ex -> new BadRequestClientException(ex,
                        (List<String>) ex.getResponseBodyAs(ProblemDetail.class).getProperties().get("errors")));
    }
}
