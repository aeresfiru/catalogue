package com.aeresfiru.catalogue.service;

import com.aeresfiru.catalogue.entity.Product;
import com.aeresfiru.catalogue.repository.ProductRepository;
import com.aeresfiru.catalogue.service.dto.CreateProductRequest;
import com.aeresfiru.catalogue.service.dto.UpdateProductRequest;
import com.aeresfiru.catalogue.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper mapper;

    @Override
    public Page<Product> findAllProducts(String filter, Pageable pageable) {
        if (StringUtils.hasText(filter)) {
            log.info("Retrieved filter parameter: {}, fetching all by filter", filter);
            return this.productRepository.findAllByTitleLikeIgnoreCase("%" + filter + "%", pageable);
        }
        log.info("No filter parameter provided, fetching all products");
        return this.productRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Product createProduct(CreateProductRequest request) {
        log.info("Creating product from request: {}", request);
        var product = this.mapper.mapToProduct(request);
        return productRepository.save(product);
    }

    @Override
    public Product findProduct(Integer productId) {
        log.info("Fetching product by id: {}", productId);
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("catalogue.errors.product.not_found"));
    }

    @Override
    @Transactional
    public Product updateProduct(UpdateProductRequest request, Integer productId) {
        log.info("Updating product ID: {}, request: {}", productId, request);
        var product = this.findProduct(productId);
        this.mapper.updateProduct(product, request);
        return product;
    }

    @Override
    @Transactional
    public void deleteProduct(Integer productId) {
        log.warn("Delete product by ID: {}", productId);
        this.productRepository.deleteById(productId);
    }
}
