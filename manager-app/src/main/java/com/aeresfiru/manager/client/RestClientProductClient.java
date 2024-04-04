package com.aeresfiru.manager.client;

import com.aeresfiru.manager.client.exception.ClientBadRequestException;
import com.aeresfiru.manager.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.manager.client.exception.ClientServerErrorException;
import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Slf4j
public class RestClientProductClient implements ProductClient {

    private static final String baseUri = "/catalogue-api/v1/products";

    private static final ParameterizedTypeReference<List<Product>> PRODUCTS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    @Override
    public List<Product> findAllProducts(String filter) {
        return executeRequest(() -> this.restClient
                .get()
                .uri(baseUri + "?filter={filter}", filter)
                .retrieve()
                .body(PRODUCTS_TYPE_REFERENCE));
    }

    @Override
    public Product createProduct(CreateProductRequest request) {
        return executeRequest(() -> this.restClient
                .post()
                .uri(baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Product.class));
    }

    @Override
    public Product findProduct(Integer productId) {
        return executeRequest(() -> this.restClient
                .get()
                .uri(baseUri + "/{productId}", productId)
                .retrieve()
                .body(Product.class));
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Integer productId) {
        return executeRequest(() -> this.restClient
                .patch()
                .uri(baseUri + "/{productId}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Product.class));
    }

    @Override
    public void deleteProduct(Integer productId) {
        executeRequest(() -> this.restClient
                .delete()
                .uri(baseUri + "/{productId}", productId)
                .retrieve()
                .toBodilessEntity());
    }

    private <T> T executeRequest(Supplier<T> requestSupplier) {
        try {
            return requestSupplier.get();
        } catch (HttpClientErrorException.NotFound ex) {
            var problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
            log.error("Request failed, server resource not found, details: {}", ex.getMessage());
            throw new ClientEntityNotFoundException(problemDetail);
        } catch (HttpClientErrorException.BadRequest ex) {
            log.error("Request failed, server return bad request: {}", ex.getMessage());
            var problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
            throw new ClientBadRequestException(problemDetail);
        } catch (HttpClientErrorException ex) {
            log.error("Request failed, server return unhandled exception: {}", ex.getMessage());
            var problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
            throw new ClientServerErrorException(problemDetail);
        }
    }
}
