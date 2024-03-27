package com.aeresfiru.customer.client;

import com.aeresfiru.customer.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebClientProductClient implements ProductClient {

    private final WebClient productWebClient;

    private static final String baseUri = "/catalogue-api/v1/products";

    @Override
    public Flux<Product> findAllProducts(String filter) {
        return this.productWebClient.get()
                .uri(baseUri + "?filter={filter}", filter)
                .retrieve()
                .bodyToFlux(Product.class);
    }

    @Override
    public Mono<Product> findProduct(Integer productId) {
        return this.productWebClient.get()
                .uri(baseUri + "/{productId}", productId)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }
}
