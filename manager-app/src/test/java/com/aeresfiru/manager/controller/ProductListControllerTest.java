package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.client.ProductClient;
import com.aeresfiru.manager.client.exception.ClientBadRequestException;
import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.shared.request.CreateProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ConcurrentModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductListControllerTest {

    @Mock
    ProductClient productClient;

    @InjectMocks
    ProductListController productController;

    @Test
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        var request = new CreateProductRequest("New product", "New product details");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(new Product(1, "New product", "New product details"))
                .when(this.productClient)
                .createProduct(new CreateProductRequest("New product", "New product details"));

        // when
        var result = this.productController.createProduct(request, model, response);

        // then
        assertThat(result).isEqualTo("redirect:/catalogue/products/1");
        verify(this.productClient).createProduct(request);
        verifyNoMoreInteractions(this.productClient);
    }

    @Test
    void createProduct_RequestIsInvalid_Returns400BadRequest() {
        // given
        var request = new CreateProductRequest("", "New product details");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("errors", List.of("Title must not be blank"));

        doThrow(new ClientBadRequestException(problemDetail)).when(this.productClient)
                .createProduct(new CreateProductRequest("", "New product details"));

        // when
        var result = this.productController.createProduct(request, model, response);

        // then
        assertThat(result).isEqualTo("catalogue/products/new_product");
        assertThat(model.containsAttribute("problemDetail")).isTrue();
        verify(this.productClient).createProduct(new CreateProductRequest("", "New product details"));
        verifyNoMoreInteractions(this.productClient);
    }

    @Test
    void getProductsList_ReturnsProductListPage() {
        // given
        String filter = "filter";
        var model = new ConcurrentModel();
        var productList = List.of(
                new Product(1, "title#1", "details#1"),
                new Product(2, "title#2", "details#2"));

        doReturn(productList).when(this.productClient).findAllProducts(filter);

        // when
        var result = this.productController.getProductsList(model, filter);

        // then
        assertThat(result).isEqualTo("catalogue/products/list");
        assertThat(model.containsAttribute("products")).isTrue();
        verify(this.productClient).findAllProducts(filter);
        verifyNoMoreInteractions(this.productClient);
    }
}
