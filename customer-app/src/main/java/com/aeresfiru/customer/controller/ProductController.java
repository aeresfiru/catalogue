package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.client.FavouriteProductClient;
import com.aeresfiru.customer.client.ProductClient;
import com.aeresfiru.customer.client.ProductReviewClient;
import com.aeresfiru.customer.client.exception.ClientBadRequestException;
import com.aeresfiru.customer.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.customer.client.payload.CreateFavouriteProductRequest;
import com.aeresfiru.customer.client.payload.CreateProductReviewRequest;
import com.aeresfiru.customer.entity.Product;
import com.aeresfiru.customer.entity.ProductReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/products/{productId:\\d+}")
@Slf4j
public class ProductController {

    private final ProductClient productClient;

    private final FavouriteProductClient favouriteProductClient;

    private final ProductReviewClient productReviewClient;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> product(@PathVariable(name = "productId") Integer productId) {
        return this.productClient.findProduct(productId)
                .switchIfEmpty(Mono.error(new ClientEntityNotFoundException("catalogue.errors.404.title")))
                .doOnError(ex -> log.error("Error retrieving product with ID: {}", productId, ex));
    }

    @ModelAttribute(name = "isFavourite", binding = false)
    public Mono<Boolean> isFavourite(@PathVariable(name = "productId") Integer productId) {
        return this.favouriteProductClient.findFavouriteProductByProductId(productId)
                .hasElement()
                .doOnError(ex -> log.error("Error checking if product is favorite with ID: {}", productId, ex));
    }

    @ModelAttribute(name = "reviews", binding = false)
    public Flux<ProductReview> productReviews(@PathVariable(name = "productId") Integer productId) {
        return this.productReviewClient.findProductReviewsByProductId(productId)
                .doOnError(ex -> log.error("Error retrieving product reviews with ID: {}", productId, ex));
    }

    @GetMapping
    public Mono<String> showProductPage() {
        return Mono.just("customer/products/product")
                .doOnError(ex -> log.error("Error rendering product page", ex));
    }

    @PostMapping("/add-to-favourites")
    public Mono<String> addProductToFavourites(@PathVariable(name = "productId") Integer productId) {
        return this.favouriteProductClient.addProductToFavourites(new CreateFavouriteProductRequest(productId))
                .thenReturn("redirect:/customer/products/%d".formatted(productId))
                .doOnError(ex -> log.error("Error adding product to favorites with ID: {}", productId, ex));
    }

    @PostMapping("/remove-from-favourites")
    public Mono<String> removeProductFromFavourites(@PathVariable(name = "productId") Integer productId) {
        return this.favouriteProductClient.removeProductFromFavourites(productId)
                .thenReturn("redirect:/customer/products/%d".formatted(productId))
                .doOnError(ex -> log.error("Error removing product from favorites with ID: {}", productId, ex));
    }

    @PostMapping("/create-review")
    public Mono<String> createReview(@ModelAttribute("product") Mono<Product> productMono,
                                     CreateProductReviewRequest request,
                                     Model model,
                                     ServerHttpResponse response) {
        return productMono.flatMap(product -> this.productReviewClient.createProductReview(request)
                        .thenReturn("redirect:/customer/products/%d".formatted(product.id()))
                        .onErrorResume(ClientBadRequestException.class, ex -> {
                            log.error("Bad request parameters provided: {}", request, ex);
                            model.addAttribute("payload", request);
                            model.addAttribute("errors", ex.getErrors());
                            response.setStatusCode(HttpStatus.BAD_REQUEST);
                            return Mono.just("customer/products/product");
                        }))
                .doOnError(ex -> log.error("Error creating product review", ex));
    }
}
