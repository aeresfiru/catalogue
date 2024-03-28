package com.aeresfiru.customer.client;

import com.aeresfiru.customer.client.exception.BadRequestClientException;
import com.aeresfiru.customer.client.payload.CreateFavouriteProductRequest;
import com.aeresfiru.customer.entity.FavouriteProduct;
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
public class WebClientFavouriteProductClient implements FavouriteProductClient {

    private final static String baseUri = "/feedback-api/v1/favourite-products";

    private final WebClient feedbackWebClient;

    @Override
    public Flux<FavouriteProduct> findAllFavouriteProducts() {
        return feedbackWebClient.get()
                .uri(baseUri)
                .retrieve()
                .bodyToFlux(FavouriteProduct.class);
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProductId(Integer productId) {
        return feedbackWebClient.get()
                .uri(baseUri + "/by-product/{productId}", productId)
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request) {
        return feedbackWebClient.post()
                .uri(baseUri)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorMap(WebClientResponseException.BadRequest.class, ex -> new BadRequestClientException(ex,
                        (List<String>) ex.getResponseBodyAs(ProblemDetail.class).getProperties().get("errors")));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(Integer productId) {
        return feedbackWebClient.delete()
                .uri(baseUri + "/by-product/{productId}", productId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
