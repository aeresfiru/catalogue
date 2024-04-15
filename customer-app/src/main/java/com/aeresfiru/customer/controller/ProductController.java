package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.client.exception.ClientBadRequestException;
import com.aeresfiru.customer.client.payload.CreateFavouriteProductRequest;
import com.aeresfiru.customer.client.payload.CreateProductReviewRequest;
import com.aeresfiru.customer.client.payload.Product;
import com.aeresfiru.customer.client.payload.ProductReview;
import com.aeresfiru.customer.service.FavouriteProductService;
import com.aeresfiru.customer.service.ProductReviewService;
import com.aeresfiru.customer.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/products/{productId:\\d+}")
@Slf4j
public class ProductController {

    private final ProductService productService;

    private final FavouriteProductService favouriteProductService;

    private final ProductReviewService productReviewService;

    @ModelAttribute
    public Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
        return exchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName())
                .doOnNext(csrfToken -> exchange.getAttributes()
                        .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, csrfToken))
                .doOnError(ex -> log.error("Csrf token not found", ex));
    }

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> product(@PathVariable(name = "productId") Integer productId) {
        return this.productService.findProduct(productId);
    }

    @ModelAttribute(name = "isFavourite", binding = false)
    public Mono<Boolean> isFavourite(@PathVariable(name = "productId") Integer productId) {
        return this.favouriteProductService.isProductInFavourites(productId);
    }

    @ModelAttribute(name = "reviews", binding = false)
    public Flux<ProductReview> productReviews(@PathVariable(name = "productId") Integer productId) {
        return this.productReviewService.findProductReviews(productId);
    }

    @GetMapping
    public Mono<String> showProductPage() {
        return Mono.just("customer/products/product");
    }

    @PostMapping("/add-to-favourites")
    public Mono<String> addProductToFavourites(@PathVariable(name = "productId") Integer productId) {
        return this.favouriteProductService.addProductToFavourites(new CreateFavouriteProductRequest(productId))
                .thenReturn("redirect:/customer/products/%d".formatted(productId));
    }

    @PostMapping("/remove-from-favourites")
    public Mono<String> removeProductFromFavourites(@PathVariable(name = "productId") Integer productId) {
        return this.favouriteProductService.removeProductFromFavourites(productId)
                .thenReturn("redirect:/customer/products/%d".formatted(productId));
    }

    @PostMapping("/create-review")
    public Mono<String> createReview(@ModelAttribute("product") Mono<Product> productMono,
                                     Mono<CreateProductReviewRequest> request,
                                     Model model,
                                     ServerHttpResponse response) {
        return Mono.zip(productMono, request)
                .flatMap(tuple -> this.productReviewService.createProductReview(tuple.getT2())
                        .thenReturn("redirect:/customer/products/%d".formatted(tuple.getT1().id()))
                        .onErrorResume(ClientBadRequestException.class, ex -> {
                            log.error("Bad request parameters provided: {}", tuple.getT2(), ex);
                            model.addAttribute("payload", tuple.getT2())
                                    .addAttribute("errors", ex.getErrors());
                            response.setStatusCode(HttpStatus.BAD_REQUEST);
                            return Mono.just("customer/products/product");
                        }));
    }
}
