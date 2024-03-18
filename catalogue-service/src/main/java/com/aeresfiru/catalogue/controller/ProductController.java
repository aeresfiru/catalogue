package com.aeresfiru.catalogue.controller;

import com.aeresfiru.catalogue.controller.resource.ProductResource;
import com.aeresfiru.catalogue.entity.Product;
import com.aeresfiru.catalogue.service.ProductService;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/catalogue-api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResource> findProducts(@RequestParam(name = "filter", required = false) String filter) {
        return this.productService.findAllProducts(filter).stream()
                .map(ProductController::mapProduct)
                .toList();
    }

    @GetMapping("/{productId}")
    public ProductResource findProductById(@PathVariable Integer productId) {
        var product = this.productService.findProduct(productId);
        return mapProduct(product);
    }

    @PostMapping
    public ResponseEntity<ProductResource> createProduct(@Valid @RequestBody CreateProductRequest request) {
        var productResource = mapProduct(this.productService.createProduct(request));
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{productId}").buildAndExpand(productResource.id()).toUri();

        return ResponseEntity.created(location).body(productResource);
    }

    @PatchMapping("/{productId}")
    public ProductResource partialUpdate(@Valid @RequestBody UpdateProductRequest request,
                                         @PathVariable("productId") Integer productId) {
        var product = this.productService.updateProductPartially(request, productId);
        return mapProduct(product);
    }

    @PutMapping("/{productId}")
    public ProductResource update(@Valid @RequestBody UpdateProductRequest request,
                                  @PathVariable("productId") Integer productId) {
        var product = this.productService.updateProduct(request, productId);
        return mapProduct(product);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Integer productId) {
        this.productService.deleteProduct(productId);
    }

    private static ProductResource mapProduct(Product product) {
        return new ProductResource(product.getId(), product.getTitle(), product.getDetails());
    }
}
