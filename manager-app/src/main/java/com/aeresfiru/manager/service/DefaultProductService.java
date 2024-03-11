package com.aeresfiru.manager.service;

import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.manager.repository.ProductRepository;
import com.aeresfiru.manager.service.dto.CreateProductRequest;
import com.aeresfiru.manager.service.dto.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
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
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
    }

    @Override
    public void updateProduct(UpdateProductRequest request, Integer productId) {
        var product = this.productRepository.findById(productId)
                .orElseThrow(NoSuchElementException::new);

        this.updateProduct(product, request);
    }

    @Override
    public void delete(Product product) {
        this.productRepository.deleteById(product.getId());
    }

    private void updateProduct(Product product, UpdateProductRequest request) {
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
