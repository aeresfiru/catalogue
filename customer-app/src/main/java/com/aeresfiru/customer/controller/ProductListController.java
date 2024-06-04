package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.service.FavouriteProductService;
import com.aeresfiru.customer.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
@RequestMapping("/customer/products")
@Slf4j
public class ProductListController {

    private final ProductService productService;
    private final FavouriteProductService favouriteProductService;

    @GetMapping("list")
    public Mono<String> showProductsListPage(
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") int pageSize,
            Model model
    ) {
        return this.productService.findAllProducts(filter, pageNumber, pageSize)
                .doOnNext(productPage -> model.addAttribute("products", productPage.content())
                        .addAttribute("products", productPage.content())
                        .addAttribute("currentPage", pageNumber)
                        .addAttribute("totalPages", productPage.totalElements() / pageSize)
                        .addAttribute("pageSize", pageSize)
                        .addAttribute("totalElements", productPage.totalElements())
                        .addAttribute("filter", filter))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favourites")
    public Mono<String> showFavouriteProductsPage(
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") int pageSize,
            Model model
    ) {
        return this.favouriteProductService.findAllFavouriteProducts(filter, pageNumber, pageSize)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products)
                        .addAttribute("filter", filter))
                .thenReturn("customer/products/favourites");
    }

    @ModelAttribute
    public Mono<CsrfToken> loadCsrfToken(ServerWebExchange exchange) {
        return exchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName())
                .doOnSuccess(token -> exchange.getAttributes()
                        .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
    }
}
