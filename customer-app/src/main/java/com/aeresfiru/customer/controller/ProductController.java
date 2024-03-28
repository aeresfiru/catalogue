package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.client.FavouriteProductClient;
import com.aeresfiru.customer.client.ProductClient;
import com.aeresfiru.customer.client.ProductReviewClient;
import com.aeresfiru.customer.client.exception.BadRequestClientException;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

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
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.errors.not_found")));
    }

    @ModelAttribute(name = "isFavourite", binding = false)
    public Mono<Boolean> isFavourite(@PathVariable(name = "productId") Integer productId) {
        return this.favouriteProductClient.findFavouriteProductByProductId(productId).hasElement();
    }

    @ModelAttribute(name = "reviews", binding = false)
    public Flux<ProductReview> productReviews(@PathVariable(name = "productId") Integer productId) {
        return this.productReviewClient.findProductReviewsByProductId(productId);
    }

    @GetMapping
    public Mono<String> showProductPage() {
        return Mono.just("customer/products/product");
    }

    @PostMapping("/add-to-favourites")
    public Mono<String> addProductToFavourites(@PathVariable Integer productId) {
        return this.favouriteProductClient.addProductToFavourites(new CreateFavouriteProductRequest(productId))
                .thenReturn("redirect:/customer/products/%d".formatted(productId))
                .doOnError(ex -> log.error(ex.getMessage(), ex))
                .onErrorResume(BadRequestClientException.class,
                        ex -> Mono.just("/customer/products/product"));
    }

    @PostMapping("/remove-from-favourites")
    public Mono<String> removeProductFromFavourites(@PathVariable Integer productId) {
        return this.favouriteProductClient.removeProductFromFavourites(productId)
                .thenReturn("redirect:/customer/products/%d".formatted(productId));
    }

    @PostMapping("/create-review")
    public Mono<String> createReview(@ModelAttribute("product") Mono<Product> productMono,
                                     CreateProductReviewRequest request,
                                     Model model,
                                     ServerHttpResponse response) {
        return productMono.flatMap(product ->
                this.productReviewClient.createProductReview(request)
                        .thenReturn("redirect:/customer/products/%d".formatted(product.id()))
                        .onErrorResume(BadRequestClientException.class, exception -> {
                            model.addAttribute("payload", request);
                            model.addAttribute("errors", exception.getErrors());
                            response.setStatusCode(HttpStatus.BAD_REQUEST);
                            return Mono.just("customer/products/product");
                        }));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Mono<String> handleNoSuchElementException(NoSuchElementException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return Mono.just("errors/404");
    }
}
