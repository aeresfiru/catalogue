package com.aeresfiru.catalogue.controller;

import com.aeresfiru.catalogue.controller.resource.ProductResource;
import com.aeresfiru.catalogue.controller.resource.ProductResourceAssembler;
import com.aeresfiru.catalogue.entity.Product;
import com.aeresfiru.catalogue.service.ProductNotFoundException;
import com.aeresfiru.catalogue.service.ProductService;
import com.aeresfiru.catalogue.service.dto.CreateProductRequest;
import com.aeresfiru.catalogue.service.dto.UpdateProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    @Mock
    ProductResourceAssembler productResourceAssembler;

    @InjectMocks
    ProductController productController;

    @Test
    void findProducts_requestIsValid_ReturnsProductList() {
        // given
        var products = List.of(
                new Product(1, "title #1", "details #1"),
                new Product(2, "title #2", "details #2"));
        String filter = "title";

        doReturn(new ProductResource(1, "title #1", "details #1"))
                .when(this.productResourceAssembler).toResource(argThat(product -> product.getId() == 1));
        doReturn(new ProductResource(2, "title #2", "details #2"))
                .when(this.productResourceAssembler).toResource(argThat(product -> product.getId() == 2));

        doReturn(new PageImpl<>(products)).when(this.productService).findAllProducts(filter, PageRequest.of(0, 10));

        // when
        var result = this.productController.findProducts(filter, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).isEqualTo(List.of(
                new ProductResource(1, "title #1", "details #1"),
                new ProductResource(2, "title #2", "details #2")
        ));
    }

    @Test
    void findProductById_ProductExists_ReturnsProductResource() {
        // given
        var product = new Product(1, "Product title", "Product description");
        var resource = new ProductResource(1, "Product title", "Product description");
        doReturn(product).when(this.productService).findProduct(1);
        doReturn(resource).when(this.productResourceAssembler).toResource(product);

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
        var resource = new ProductResource(1, "title", "details");
        doReturn(product).when(this.productService).createProduct(request);
        doReturn(resource).when(this.productResourceAssembler).toResource(product);

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
    void update_requestIsValid_ReturnsProduct() {
        // given
        var request = new UpdateProductRequest("Updated title", null);
        var resource = new ProductResource(1, "Updated title", "details");
        var product = new Product(1, "Updated title", "details");
        doReturn(product).when(this.productService).updateProduct(request, 1);
        doReturn(resource).when(this.productResourceAssembler).toResource(product);

        // when
        var result = this.productController.update(request, 1);

        // then
        assertThat(result).isEqualTo(new ProductResource(1, "Updated title", "details"));

        verify(this.productService).updateProduct(request, 1);
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
