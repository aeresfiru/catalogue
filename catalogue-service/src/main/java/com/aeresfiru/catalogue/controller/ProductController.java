package com.aeresfiru.catalogue.controller;

import com.aeresfiru.catalogue.controller.resource.ProductResource;
import com.aeresfiru.catalogue.controller.resource.ProductResourceAssembler;
import com.aeresfiru.catalogue.service.ProductService;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/catalogue-api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductResourceAssembler resourceAssembler;

    @GetMapping
    public Page<ProductResource> findProducts(@RequestParam(name = "filter", required = false) String filter,
                                              @PageableDefault Pageable pageable) {
        return this.productService.findAllProducts(filter, pageable)
                .map(this.resourceAssembler::toResource);
    }

    @GetMapping("/{productId}")
    public ProductResource findProductById(@PathVariable Integer productId) {
        var product = this.productService.findProduct(productId);
        return this.resourceAssembler.toResource(product);
    }

    @PostMapping
    public ResponseEntity<ProductResource> createProduct(@Valid @RequestBody CreateProductRequest request,
                                                         UriComponentsBuilder uriComponentsBuilder) {
        var product = this.productService.createProduct(request);
        var model = resourceAssembler.toResource(product);
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("/catalogue-api/v1/products/{productId}")
                        .build(Map.of("productId", product.getId())))
                .body(model);
    }

    @PatchMapping("/{productId}")
    public ProductResource update(@Valid @RequestBody UpdateProductRequest request,
                                  @PathVariable("productId") Integer productId) {
        var product = this.productService.updateProduct(request, productId);
        return this.resourceAssembler.toResource(product);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Integer productId) {
        this.productService.deleteProduct(productId);
    }
}
