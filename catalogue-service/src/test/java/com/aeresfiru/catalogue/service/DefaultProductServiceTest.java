package com.aeresfiru.catalogue.service;

import com.aeresfiru.catalogue.entity.Product;
import com.aeresfiru.catalogue.repository.ProductRepository;
import com.aeresfiru.catalogue.service.mapper.ProductMapper;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMapper productMapper;

    @InjectMocks
    DefaultProductService productService;

    @Test
    void findProduct_ProductExists_ReturnsProduct() {
        // given
        var product = new Product(1, "title", "details");
        doReturn(Optional.of(product)).when(this.productRepository).findById(1);

        // when
        var result = this.productService.findProduct(1);

        // then
        assertThat(result).isEqualTo(new Product(1, "title", "details"));

        verify(this.productRepository).findById(1);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findProduct_ProductDoesNotExists_ThrowsNoSuchElementException() {
        // given
        doReturn(Optional.empty()).when(this.productRepository).findById(1);

        // then
        assertThatThrownBy(() -> this.productService.findProduct(1))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void findAllProducts_WithFilter_ReturnsFilteredProducts() {
        // given
        var productPage = new PageImpl<>(List.of(new Product(1, "filtered", "details")));
        var pageable = PageRequest.of(0, 2);
        doReturn(productPage).when(this.productRepository).findAllByTitleLikeIgnoreCase("%filter%", pageable);

        // when
        var result = this.productService.findAllProducts("filter", pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).containsExactly(new Product(1, "filtered", "details"));

        verify(this.productRepository).findAllByTitleLikeIgnoreCase("%filter%", pageable);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findAllProducts_WithoutFilter_ReturnsAllProducts() {
        // given
        var productPage = new PageImpl<>(List.of(
                new Product(1, "title#1", "details#1"),
                new Product(2, "title#2", "details#2")));
        var pageable = PageRequest.of(0, 2);
        doReturn(productPage).when(this.productRepository).findAll(pageable);

        // when
        var result = this.productService.findAllProducts(null, pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).containsExactlyInAnyOrder(
                new Product(1, "title#1", "details#1"),
                new Product(2, "title#2", "details#2"));

        verify(this.productRepository).findAll(pageable);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void createProduct_ReturnsCreatedProduct() {
        // given
        var request = new CreateProductRequest("title", "details");
        var productRequest = new Product(null, "title", "details");
        doReturn(productRequest).when(this.productMapper).mapToProduct(request);
        doReturn(new Product(1, "title", "details"))
                .when(this.productRepository).save(new Product(null, "title", "details"));

        // when
        var result = this.productService.createProduct(request);

        // then
        assertThat(result).isEqualTo(new Product(1, "title", "details"));

        verify(this.productRepository).save(new Product(null, "title", "details"));
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void updateProduct_ProductExists_ReturnsUpdatedProduct() {
        // given
        var request = new UpdateProductRequest("Updated title", null);
        doReturn(Optional.of(new Product(1, "Updated title", "details")))
                .when(this.productRepository).findById(1);

        // when
        var result = this.productService.updateProduct(request, 1);

        // then
        assertThat(result).isEqualTo(new Product(1, "Updated title", "details"));
        verify(this.productRepository).findById(1);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void updateProduct_ProductDoesNotExists_ThrowsNoSuchElementException() {
        // given
        var request = new UpdateProductRequest("Updated title", null);
        doReturn(Optional.empty()).when(this.productRepository).findById(1);

        // then
        assertThatThrownBy(() -> this.productService.updateProduct(request, 1))
                .isInstanceOf(ProductNotFoundException.class);

        verify(this.productRepository).findById(1);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void deleteProduct_DoesNotThrowAnyException() {
        // given
        doNothing().when(this.productRepository).deleteById(1);

        // then
        assertThatNoException().isThrownBy(() -> this.productService.deleteProduct(1));

        verify(this.productRepository).deleteById(1);
        verifyNoMoreInteractions(this.productRepository);
    }
}
