package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.entity.Product;
import com.aeresfiru.customer.service.FavouriteProductService;
import com.aeresfiru.customer.service.ProductService;
import com.aeresfiru.shared.client.PageApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ProductListControllerTest {

    @Mock
    ProductService productService;

    @Mock
    FavouriteProductService favouriteProductService;

    @InjectMocks
    ProductListController controller;

    @Test
    void showProductsListPage_RequestIsValid_ReturnsProductListPage() {
        // given
        var model = new ConcurrentModel();
        doReturn(Mono.just(new PageApiResponse<>(List.of(
                new Product(1, "title#1", "details#1"),
                new Product(2, "title#2", "details#2")
        ), 10, 0, 2, 1))).when(this.productService).findAllProducts("filter", 0, 10);

        // when
        StepVerifier.create(this.controller.showProductsListPage("filter", 0, 10, model))
                .expectNext("customer/products/list")
                .verifyComplete();

        assertThat(model.getAttribute("filter")).isEqualTo("filter");
        assertThat(model.getAttribute("products")).isEqualTo(List.of(
                new Product(1, "title#1", "details#1"),
                new Product(2, "title#2", "details#2")
        ));
    }

    @Test
    void showFavouriteProductsPage_RequestIsValid_ReturnsFavouriteProductsPage() {
        // given
        var model = new ConcurrentModel();
        doReturn(Flux.just(
                new Product(1, "title#1", "details#1"),
                new Product(2, "title#1", "details#1"))
        ).when(this.favouriteProductService).findAllFavouriteProducts("filter", 0, 10);

        // when
        StepVerifier.create(this.controller.showFavouriteProductsPage("filter", 0, 10, model))
                // then
                .expectNext("customer/products/favourites")
                .verifyComplete();

        assertThat(model.getAttribute("filter")).isEqualTo("filter");
        assertThat(model.getAttribute("products")).isEqualTo(List.of(
                new Product(1, "title#1", "details#1"),
                new Product(2, "title#1", "details#1")
        ));
    }
}