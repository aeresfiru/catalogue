package com.aeresfiru.catalogue.controller;

import com.aeresfiru.catalogue.controller.resource.ProductResource;
import com.aeresfiru.catalogue.entity.Product;
import com.aeresfiru.catalogue.service.ProductNotFoundException;
import com.aeresfiru.catalogue.service.ProductService;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductController productController;

    @Test
    void findProducts_requestIsValid_ReturnsProductList() {
        // given
        var productList = List.of(
                new Product(1, "title #1", "details #1"),
                new Product(2, "title #2", "details #2"));
        var filter = "title";
        doReturn(productList).when(this.productService).findAllProducts(filter);

        // when
        var result = this.productController.findProducts(filter);

        // then
        assertThat(result).isEqualTo(List.of(
                new ProductResource(1, "title #1", "details #1"),
                new ProductResource(2, "title #2", "details #2")
        ));
    }

    @Test
    void findProductById_ProductExists_ReturnsProductResource() {
        // given
        var product = new Product(1, "Product title", "Product description");
        doReturn(product).when(this.productService).findProduct(1);

        // when
        var result = this.productController.findProductById(1);

        // then
        assertThat(result).isEqualTo(new ProductResource(1, "Product title", "Product description"));
    }

    @Test
    void findProductById_ProductDoesNotExist_ReturnsProblemDetails() {
        // given
        doThrow(ProductNotFoundException.class).when(this.productService).findProduct(1);

        // then
        assertThatThrownBy(() -> this.productController.findProductById(1))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void createProduct_RequestIsValid_ReturnsProduct() {
        // given
        var product = new Product(1, "title", "details");
        var request = new CreateProductRequest("title", "details");
        var uriComponentsBuilder = UriComponentsBuilder.fromUri(URI.create("/catalogue-api/v1/products"));
        doReturn(product).when(this.productService).createProduct(request);

        // when
        var result = this.productController.createProduct(request, uriComponentsBuilder);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getHeaders().get(HttpHeaders.LOCATION)).isNotNull();
        assertThat(result.getBody()).isEqualTo(new ProductResource(1, "title", "details"));

        verify(this.productService).createProduct(request);
        verifyNoMoreInteractions(this.productService);
    }

    @Test
    void partialUpdate_requestIsValid_ReturnsProduct() {
        // given
        var request = new UpdateProductRequest("Updated title", null);
        doReturn(new Product(1, "Updated title", "details"))
                .when(this.productService).updateProductPartially(request, 1);

        // when
        var result = this.productController.partialUpdate(request, 1);

        // then
        assertThat(result).isEqualTo(new ProductResource(1, "Updated title", "details"));

        verify(this.productService).updateProductPartially(request, 1);
        verifyNoMoreInteractions(this.productService);
    }

    @Test
    void deleteProduct_ProductExists_ReturnsNoContent() {
        // given
        doNothing().when(this.productService).deleteProduct(1);

        // then
        assertThatNoException().isThrownBy(() -> this.productController.deleteProduct(1));

        verify(this.productService).deleteProduct(1);
        verifyNoMoreInteractions(this.productService);
    }
}
