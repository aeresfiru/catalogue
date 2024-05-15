package com.aeresfiru.manager.client;

import com.aeresfiru.manager.client.payload.CreateProductRequest;
import com.aeresfiru.manager.client.payload.PageResponse;
import com.aeresfiru.manager.client.payload.Product;
import com.aeresfiru.manager.client.payload.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestClientProductClient implements ProductClient {

    private static final String baseUri = "/catalogue-api/v1/products";

    private static final ParameterizedTypeReference<PageResponse<Product>> PRODUCTS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient catalogueRestClient;

    private final RequestExecutor requestExecutor;

    @Override
    public PageResponse<Product> findAllProducts(String filter, int page, int size) {
        return this.requestExecutor.execute(() -> this.catalogueRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri)
                        .queryParam("filter", filter)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(PRODUCTS_TYPE_REFERENCE));
    }

    @Override
    public Product createProduct(CreateProductRequest request) {
        return this.requestExecutor.execute(() -> this.catalogueRestClient
                .post()
                .uri(baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Product.class));
    }

    @Override
    public Product findProduct(Integer productId) {
        return this.requestExecutor.execute(() -> this.catalogueRestClient
                .get()
                .uri(baseUri + "/{productId}", productId)
                .retrieve()
                .body(Product.class));
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Integer productId) {
        return this.requestExecutor.execute(() -> this.catalogueRestClient
                .patch()
                .uri(baseUri + "/{productId}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Product.class));
    }

    @Override
    public void deleteProduct(Integer productId) {
        this.requestExecutor.execute(() -> this.catalogueRestClient
                .delete()
                .uri(baseUri + "/{productId}", productId)
                .retrieve()
                .toBodilessEntity());
    }
}
