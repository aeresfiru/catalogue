package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.client.ProductClient;
import com.aeresfiru.manager.client.exception.ClientBadRequestException;
import com.aeresfiru.manager.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.shared.request.UpdateProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ConcurrentModel;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProductControllerTest {

    @Mock
    ProductClient productClient;

    @InjectMocks
    ProductController controller;

    @Test
    void product_ProductExists_ReturnsProduct() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");

        doReturn(product).when(this.productClient).findProduct(1);

        // when
        var result = this.controller.product(1);

        // then
        assertThat(result).isEqualTo(product);

        verify(this.productClient).findProduct(1);
        verifyNoMoreInteractions(this.productClient);
    }

    @Test
    void product_ProductDoesNotExist_ThrowsClientEntityNotFoundException() {
        // given
        doThrow(ClientEntityNotFoundException.class).when(this.productClient).findProduct(1);

        // when
        assertThatThrownBy(() -> this.controller.product(1))
                .isInstanceOf(ClientEntityNotFoundException.class);

        // then
        verify(this.productClient).findProduct(1);
        verifyNoMoreInteractions(this.productClient);
    }

    @Test
    void getProduct_ReturnsProductPage() {
        // given

        // when
        var result = this.controller.getProduct();

        // then
        assertThat(result).isEqualTo("catalogue/products/product");

        verifyNoInteractions(this.productClient);
    }

    @Test
    void getProductEditPage_ReturnsProductEditPage() {
        // given

        // when
        var result = this.controller.getProductEditPage();

        // then
        assertThat(result).isEqualTo("catalogue/products/edit");

        verifyNoInteractions(this.productClient);
    }

    @Test
    void updateProduct_RequestIsValid_ReturnsProductPage() {
        // given
        var request = new UpdateProductRequest("New title", "New details");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(new Product(1, "New title", "New details"))
                .when(this.productClient)
                .updateProduct(new UpdateProductRequest("New title", "New details"), 1);

        // when
        var result = this.controller.updateProduct(1, request, model, response);

        // then
        assertThat(result).isEqualTo("redirect:/catalogue/products/1");
        verify(this.productClient).updateProduct(request, 1);
        verifyNoMoreInteractions(this.productClient);
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsProductPage() {
        // given
        var request = new UpdateProductRequest("", null);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        doThrow(new ClientBadRequestException(problemDetail))
                .when(this.productClient).updateProduct(request, 1);

        // when
        var result = this.controller.updateProduct(1, request, model, response);

        // then
        assertThat(result).isEqualTo("catalogue/products/edit");
        assertThat(model.containsAttribute("problemDetail")).isTrue();
        verify(this.productClient).updateProduct(request, 1);
        verifyNoMoreInteractions(this.productClient);
    }

    @Test
    void updateProduct_ProductDoesNotExist_ReturnsProductPage() {
        // given
        var request = new UpdateProductRequest("title", "details");
        var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        doThrow(new ClientEntityNotFoundException(problemDetail))
                .when(this.productClient).updateProduct(request, 1);

        // when
        assertThatThrownBy(() -> this.productClient.updateProduct(request, 1))
                .isInstanceOf(ClientEntityNotFoundException.class);

        // then
        verify(this.productClient).updateProduct(request, 1);
        verifyNoMoreInteractions(this.productClient);
    }

    @Test
    void deleteProduct_RequestIsValid_ReturnsProductListPage() {
        // when
        var result = this.controller.deleteProduct(1);

        assertThat(result).isEqualTo("redirect:/catalogue/products/list");
        verify(this.productClient).deleteProduct(1);
        verifyNoMoreInteractions(this.productClient);
    }
}
