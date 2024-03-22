package com.aeresfiru.catalogue.service;

import com.aeresfiru.catalogue.entity.Product;
import com.aeresfiru.catalogue.repository.ProductRepository;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> findAllProducts(String filter) {
        if (StringUtils.hasText(filter)) {
            return this.productRepository.findAllByTitleLikeIgnoreCase("%" + filter + "%");
        }
        return this.productRepository.findAll();
    }

    @Override
    @Transactional
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
    @Transactional
    public Product updateProductPartially(UpdateProductRequest request, Integer productId) {
        var product = this.findProduct(productId);
        updateProductFieldsPartially(product, request);
        return product;
    }

    @Override
    @Transactional
    public void deleteProduct(Integer productId) {
        this.productRepository.deleteById(productId);
    }

    @Override
    @Transactional
    public Product updateProduct(UpdateProductRequest request, Integer productId) {
        var product = this.findProduct(productId);
        updateProductFields(product, request);
        return product;
    }

    private static void updateProductFields(Product product, UpdateProductRequest request) {
        product.setTitle(request.title());
        product.setDetails(request.details());
    }

    private static void updateProductFieldsPartially(Product product, UpdateProductRequest request) {
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
