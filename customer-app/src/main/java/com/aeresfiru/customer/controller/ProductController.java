package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.client.ProductClient;
import com.aeresfiru.customer.entity.Product;
import com.aeresfiru.customer.entity.ProductReview;
import com.aeresfiru.customer.service.FavouriteProductService;
import com.aeresfiru.customer.service.ProductReviewService;
import com.aeresfiru.customer.service.dto.ProductReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
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
public class ProductController {

    private final ProductClient productClient;
    private final FavouriteProductService favouriteService;
    private final ProductReviewService reviewService;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> product(@PathVariable(name = "productId") Integer productId) {
        return this.productClient.findProduct(productId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.errors.not_found")));
    }

    @ModelAttribute(name = "isFavourite", binding = false)
    public Mono<Boolean> isFavourite(@PathVariable(name = "productId") Integer productId) {
        return this.favouriteService.isFavouriteProduct(productId);
    }

    @ModelAttribute(name = "reviews", binding = false)
    public Flux<ProductReview> productReviews(@PathVariable(name = "productId") Integer productId) {
        return this.reviewService.findAllProductReviews(productId);
    }

    @GetMapping
    public Mono<String> showProductPage() {
        return Mono.just("customer/products/product");
    }

    @PostMapping("/add-to-favourites")
    public Mono<String> addProductToFavourites(@PathVariable Integer productId) {
        return this.favouriteService.addProductToFavourites(productId)
                .thenReturn("redirect:/customer/products/%d".formatted(productId));
    }

    @PostMapping("/remove-from-favourites")
    public Mono<String> removeProductFromFavourites(@PathVariable Integer productId) {
        return this.favouriteService.removeProductFromFavourites(productId)
                .thenReturn("redirect:/customer/products/%d".formatted(productId));
    }

    @PostMapping("/create-review")
    public Mono<String> createReview(@PathVariable Integer productId,
                                     @Validated ProductReviewRequest request,
                                     BindingResult errors,
                                     Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("payload", request);
            model.addAttribute("errors", errors.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage).toList());
            return Mono.just("customer/products/product");
        }
        return this.reviewService.createProductReview(productId, request.rating(), request.review())
                .thenReturn("redirect:/customer/products/" + productId);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Mono<String> handleNoSuchElementException(NoSuchElementException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return Mono.just("errors/404");
    }
}
