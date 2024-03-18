package com.aeresfiru.manager.client;

import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRestClientImpl implements ProductRestClient {

    private static final String baseUri = "/catalogue-api/v1/products";

    private static final ParameterizedTypeReference<List<Product>> PRODUCTS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    @Override
    public List<Product> findAllProducts(String filter) {
        return this.restClient
                .get()
                .uri(baseUri + "?filter={filter}", filter)
                .retrieve()
                .body(PRODUCTS_TYPE_REFERENCE);
    }

    @Override
    public Result<Product, ProblemDetail> createProduct(CreateProductRequest request) {
        return executeRequest(() -> this.restClient
                .post()
                .uri(baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Product.class));
    }

    @Override
    public Result<Product, ProblemDetail> findProduct(Integer productId) {
        return executeRequest(() -> this.restClient
                .get()
                .uri(baseUri + "/{productId}", productId)
                .retrieve()
                .body(Product.class));
    }

    @Override
    public Result<Product, ProblemDetail> updateProduct(UpdateProductRequest request, Integer productId) {
        return executeRequest(() -> this.restClient
                .put()
                .uri(baseUri + "/{productId}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Product.class));
    }

    @Override
    public Result<Void, ProblemDetail> deleteProduct(Integer productId) {
        return executeRequest(() -> this.restClient
                .delete()
                .uri(baseUri + "/{productId}", productId)
                .retrieve()
                .toBodilessEntity()
                .getBody());
    }

    private <T> Result<T, ProblemDetail> executeRequest(Supplier<T> requestSupplier) {
        try {
            T result = requestSupplier.get();
            return Result.success(result);
        } catch (HttpClientErrorException ex) {
            var problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
            log.error("Request failed: {}", problemDetail);
            return Result.failure(problemDetail);
        }
    }
}
