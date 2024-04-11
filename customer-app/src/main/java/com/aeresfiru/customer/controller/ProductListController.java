package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.service.FavouriteProductService;
import com.aeresfiru.customer.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
@RequestMapping("/customer/products")
@Slf4j
public class ProductListController {

    private final ProductService productService;
    private final FavouriteProductService favouriteProductService;

    @GetMapping("list")
    public Mono<String> showProductsListPage(@RequestParam(name = "filter", required = false) String filter,
                                             @RequestParam(name = "page", defaultValue = "0") Integer pageNumber,
                                             @RequestParam(name = "size", defaultValue = "10") Integer pageSize,
                                             Model model) {
        return this.productService.findAllProducts(filter, pageNumber, pageSize)
                .doOnNext(page -> model.addAttribute("products", page.getContent())
                        .addAttribute("filter", filter)
                        .addAttribute("page", page.getNumber())
                        .addAttribute("size", page.getSize()))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favourites")
    public Mono<String> showFavouriteProductsPage(@RequestParam(name = "filter", required = false) String filter,
                                                  @RequestParam(name = "page", defaultValue = "0") Integer pageNumber,
                                                  @RequestParam(name = "size", defaultValue = "10") Integer pageSize,
                                                  Model model) {
        return this.favouriteProductService.findAllFavouriteProducts(filter, pageNumber, pageSize)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products)
                        .addAttribute("filter", filter))
                .thenReturn("customer/products/favourites");
    }
}
