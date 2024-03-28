package com.aeresfiru.customer.controller;

import com.aeresfiru.customer.client.FavouriteProductClient;
import com.aeresfiru.customer.client.ProductClient;
import com.aeresfiru.customer.entity.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
@RequestMapping("/customer/products")
public class ProductListController {

    private final ProductClient productClient;
    private final FavouriteProductClient favouriteProductClient;

    @GetMapping("list")
    public Mono<String> showProductsListPage(@RequestParam(name = "filter", required = false) String filter,
                                             Model model) {
        return this.productClient.findAllProducts(filter)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products)
                        .addAttribute("filter", filter))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favourites")
    public Mono<String> showFavouriteProductsPage(@RequestParam(name = "filter", required = false) String filter,
                                                  Model model) {
        return this.favouriteProductClient.findAllFavouriteProducts()
                .map(FavouriteProduct::productId)
                .collectList()
                .flatMap(favouriteProducts -> this.productClient.findAllProducts(filter)
                        .filter(product -> favouriteProducts.contains(product.id()))
                        .collectList()
                        .doOnNext(products -> model.addAttribute("products", products)
                                .addAttribute("filter", filter)))
                .thenReturn("customer/products/favourites");
    }
}
