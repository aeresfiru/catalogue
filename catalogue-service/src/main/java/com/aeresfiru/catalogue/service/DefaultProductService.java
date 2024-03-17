package com.aeresfiru.catalogue.service;

import com.aeresfiru.catalogue.entity.Product;
import com.aeresfiru.catalogue.repository.ProductRepository;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> findAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public Product createProduct(CreateProductRequest request) {
        var product = mapToProduct(request);
        return productRepository.save(product);
    }

    @Override
    public Product findProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("catalogue.errors.product.not_found"));
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Integer productId) {
        var product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("catalogue.errors.product.not_found"));
        this.updateProductFields(product, request);
        return product;
    }

    @Override
    public void deleteProduct(Integer productId) {
        this.productRepository.deleteById(productId);
    }

    private void updateProductFields(Product product, UpdateProductRequest request) {
        if (Objects.nonNull(request.title())) {
            product.setTitle(request.title());
        }
        if (Objects.nonNull(request.details())) {
            product.setDetails(request.details());
        }
    }

    private static Product mapToProduct(CreateProductRequest request) {
        return new Product(null, request.title(), request.details());
    }
}
