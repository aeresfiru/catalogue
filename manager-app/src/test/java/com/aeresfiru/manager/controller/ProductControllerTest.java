package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.client.ProductRestClient;
import com.aeresfiru.manager.client.Result;
import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
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
class ProductControllerTest {

    @Mock
    ProductRestClient productRestClient;

    @InjectMocks
    ProductController productController;

    @Test
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        var request = new CreateProductRequest("New product", "New product details");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(Result.success(new Product(1, "New product", "New product details")))
                .when(this.productRestClient)
                .createProduct(new CreateProductRequest("New product", "New product details"));

        // when
        var result = this.productController.createProduct(request, model, response);

        // then
        assertThat(result).isEqualTo("redirect:/catalogue/products/1");
        verify(this.productRestClient).createProduct(request);
        verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void createProduct_RequestIsInvalid_Returns400BadRequest() {
        // given
        var request = new CreateProductRequest("", "New product details");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("errors", List.of("Title must not be blank"));

        doReturn(Result.failure(problemDetail))
                .when(this.productRestClient)
                .createProduct(new CreateProductRequest("", "New product details"));

        // when
        var result = this.productController.createProduct(request, model, response);

        // then
        assertThat(result).isEqualTo("catalogue/products/new_product");
        assertThat(model.containsAttribute("problemDetail")).isTrue();
        verify(this.productRestClient).createProduct(new CreateProductRequest("", "New product details"));
        verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void getProductsList_ReturnsProductListPage() {
        // given
        String filter = "filter";
        var model = new ConcurrentModel();
        var productList = List.of(
                new Product(1, "title#1", "details#1"),
                new Product(2, "title#2", "details#2"));

        doReturn(productList).when(this.productRestClient).findAllProducts(filter);

        // when
        var result = this.productController.getProductsList(model, filter);

        // then
        assertThat(result).isEqualTo("catalogue/products/list");
        assertThat(model.containsAttribute("products")).isTrue();
        verify(this.productRestClient).findAllProducts(filter);
        verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void getProduct_ProductExists_ReturnsProductPage() {
        // given
        var model = new ConcurrentModel();
        var product = new Product(1, "title", "details");
        var resp = new MockHttpServletResponse();

        doReturn(Result.success(product)).when(this.productRestClient).findProduct(1);

        // when
        var result = this.productController.getProduct(1, model, resp);

        // then
        assertThat(result).isEqualTo("catalogue/products/product");
        assertThat(model.getAttribute("product")).isEqualTo(product);
        verify(this.productRestClient).findProduct(1);
        verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void getProduct_ProductDoesNotExist_Return404() {
        // given
        var model = new ConcurrentModel();
        var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        var resp = new MockHttpServletResponse();

        doReturn(Result.failure(problemDetail)).when(this.productRestClient).findProduct(1);

        // when
        var result = this.productController.getProduct(1, model, resp);

        // then
        assertThat(result).isEqualTo("errors/404");
        assertThat(model.containsAttribute("problemDetail")).isTrue();
        verify(this.productRestClient).findProduct(1);
        verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void getProductEditPage_ProductExists_ReturnsEditProductPage() {
        // given
        var model = new ConcurrentModel();
        var product = new Product(1, "title", "details");
        var resp = new MockHttpServletResponse();

        doReturn(Result.success(product)).when(this.productRestClient).findProduct(1);

        // when
        var result = this.productController.getProductEditPage(1, model, resp);

        // then
        assertThat(result).isEqualTo("catalogue/products/edit");
        assertThat(model.getAttribute("product")).isEqualTo(product);
        verify(this.productRestClient).findProduct(1);
        verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void getProductEditPage_ProductDoesNotExist_Returns404() {
        // given
        var model = new ConcurrentModel();
        var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        var resp = new MockHttpServletResponse();

        doReturn(Result.failure(problemDetail)).when(this.productRestClient).findProduct(1);

        // when
        var result = this.productController.getProductEditPage(1, model, resp);

        // then
        assertThat(result).isEqualTo("errors/404");
        assertThat(model.containsAttribute("problemDetail")).isTrue();
        verify(this.productRestClient).findProduct(1);
        verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void updateProduct_RequestIsValid_ReturnsProductPage() {
        // given
        var request = new UpdateProductRequest("New title", "New details");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(Result.success(new Product(1, "title", "details")))
                .when(this.productRestClient)
                .findProduct(1);

        doReturn(Result.success(new Product(1, "New title", "New details")))
                .when(this.productRestClient)
                .updateProduct(new UpdateProductRequest("New title", "New details"), 1);

        // when
        var result = this.productController.updateProduct(1, request, model, response);

        // then
        assertThat(result).isEqualTo("redirect:/catalogue/products/1");
        verify(this.productRestClient).findProduct(1);
        verify(this.productRestClient).updateProduct(request, 1);
        verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsProductPage() {
        // given
        var request = new UpdateProductRequest("", null);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(Result.success(new Product(1, "title", "details")))
                .when(this.productRestClient)
                .findProduct(1);

        doReturn(Result.failure(ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)))
                .when(this.productRestClient)
                .updateProduct(new UpdateProductRequest("", null), 1);

        // when
        var result = this.productController.updateProduct(1, request, model, response);

        // then
        assertThat(result).isEqualTo("catalogue/products/edit");
        assertThat(model.containsAttribute("problemDetail")).isTrue();
        verify(this.productRestClient).findProduct(1);
        verify(this.productRestClient).updateProduct(request, 1);
        verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void updateProduct_ProductDoesNotExist_ReturnsProductPage() {
        // given
        var request = new UpdateProductRequest("title", "details");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(Result.failure(ProblemDetail.forStatus(HttpStatus.NOT_FOUND)))
                .when(this.productRestClient)
                .findProduct(1);

        // when
        var result = this.productController.updateProduct(1, request, model, response);

        // then
        assertThat(result).isEqualTo("errors/404");
        assertThat(model.containsAttribute("problemDetail")).isTrue();
        verify(this.productRestClient).findProduct(1);
        verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void deleteProduct_RequestIsValid_ReturnsProductListPage() {
        // given
        var model = new ConcurrentModel();
        doReturn(mock(Result.class)).when(this.productRestClient).deleteProduct(1);

        // when
        var result = this.productController.deleteProduct(1, model);

        assertThat(result).isEqualTo("redirect:/catalogue/products/list");
        verify(this.productRestClient).deleteProduct(1);
        verifyNoMoreInteractions(this.productRestClient);
    }
}
