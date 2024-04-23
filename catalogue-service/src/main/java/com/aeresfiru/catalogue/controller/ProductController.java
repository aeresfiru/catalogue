package com.aeresfiru.catalogue.controller;

import com.aeresfiru.catalogue.controller.resource.ProductResource;
import com.aeresfiru.catalogue.controller.resource.ProductResourceAssembler;
import com.aeresfiru.catalogue.service.ProductService;
import com.aeresfiru.catalogue.service.dto.CreateProductRequest;
import com.aeresfiru.catalogue.service.dto.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/catalogue-api/v1/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "keycloak")
public class ProductController {

    private final ProductService productService;
    private final ProductResourceAssembler resourceAssembler;

    @GetMapping
    @Operation(
            summary = "Find Products",
            description = "Retrieve a list of products with optional filtering and pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class))
            )
    })
    public Page<ProductResource> findProducts(
            @Parameter(description = "Product title filter") @RequestParam(name = "filter", required = false) String filter,
            @Parameter(description = "Page number") @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return productService.findAllProducts(filter, PageRequest.of(page, size))
                .map(resourceAssembler::toResource);
    }

    @GetMapping("/{productId}")
    @Operation(
            summary = "Find Product by ID",
            description = "Retrieve product details by ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResource.class)
                    )
            )
    })
    public ProductResource findProductById(
            @Parameter(description = "Product ID") @PathVariable Integer productId
    ) {
        var product = productService.findProduct(productId);
        return resourceAssembler.toResource(product);
    }

    @PostMapping
    @Operation(
            summary = "Create Product",
            description = "Create a new product."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResource.class))
            )
    })
    public ResponseEntity<ProductResource> createProduct(
            @Parameter(description = "Product details") @Valid @RequestBody CreateProductRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        var product = productService.createProduct(request);
        var model = resourceAssembler.toResource(product);
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("/catalogue-api/v1/products/{productId}")
                        .build(Map.of("productId", product.getId())))
                .body(model);
    }

    @PatchMapping("/{productId}")
    @Operation(
            summary = "Update Product",
            description = "Update an existing product."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResource.class)
                    )
            )
    })
    public ProductResource update(
            @Parameter(description = "Product ID") @Valid @RequestBody UpdateProductRequest request,
            @PathVariable("productId") Integer productId
    ) {
        var product = productService.updateProduct(request, productId);
        return resourceAssembler.toResource(product);
    }

    @DeleteMapping("/{productId}")
    @Operation(
            summary = "Delete Product",
            description = "Delete an existing product."
    )
    @ApiResponse(
            responseCode = "204",
            description = "No content"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Integer productId
    ) {
        productService.deleteProduct(productId);
    }
}
